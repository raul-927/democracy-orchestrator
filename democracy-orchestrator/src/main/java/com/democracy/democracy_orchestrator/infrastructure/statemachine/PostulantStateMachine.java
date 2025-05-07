package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.guard.ReactiveGuard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Configuration
@EnableStateMachineFactory(name ="postulantStateMachineFactory")
public class PostulantStateMachine extends EnumStateMachineConfigurerAdapter<PostulationStates, PostulationEvents> {

    @Autowired
    private PostulantTriggerImpl postulantTrigger;

    @Autowired
    private PersonService personService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    private Profession profession;

    private Document document;

    private Investigation investigation;

    @Override
    public void configure(StateMachineStateConfigurer<PostulationStates, PostulationEvents> states)throws Exception{
        states
                .withStates()
                .initial(PostulationStates.NEW)
                //.state(PostulationStates.DOCUMENTS_VALIDATED)
                .states(EnumSet.allOf(PostulationStates.class))
                .end(PostulationStates.COMPLETED)
                .end(PostulationStates.CANCELLED)
                .end(PostulationStates.NOT_VALID);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PostulationStates, PostulationEvents> transitions)throws Exception{
        transitions
                .withExternal()
                    .source(PostulationStates.NEW)
                        .target(PostulationStates.PERSON_VALIDATED)
                            .event(PostulationEvents.VALIDATE_PERSON)
                                .action(validatePersonAction())
                .and()
                .withExternal()
                    .source(PostulationStates.PERSON_VALIDATED)
                        .target(PostulationStates.PROFESSION_VALIDATED)
                            .event(PostulationEvents.VALIDATE_PROFESSION)
                                .action(validateProfessionAction())
                .and()
                .withExternal()
                    .source(PostulationStates.PROFESSION_VALIDATED)
                        .target(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                            .event(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                                .action(criminalRecordsAction())
                /*.and()
                .withChoice()
                    .source(PostulationStates.PERSON_VALIDATED)
                        .first(PostulationStates.PROFESSION_VALIDATED, guardValidatePersons())
                            .last(PostulationStates.NOT_VALID)*/
                .and()
                .withExternal()
                    .source(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                        .target(PostulationStates.QUALIFICATION_VALIDATED)
                            .event(PostulationEvents.VALIDATE_QUALIFICATION)
                                .action(validateQualificationAction())
                .and()
                .withExternal()
                    .source(PostulationStates.QUALIFICATION_VALIDATED)
                        .target(PostulationStates.DOCUMENTS_VALIDATED)
                            .event(PostulationEvents.VALIDATE_DOCUMENTS)
                                .action(validateDocumentAction())
                .and()
                .withExternal()
                    .source(PostulationStates.DOCUMENTS_VALIDATED)
                        .target(PostulationStates.COMPLETED)
                            .event(PostulationEvents.COMPLETE)
                                .action(completedAction())
                .and()
                .withExternal()
                    .source(PostulationStates.PROFESSION_VALIDATED)
                        .target(PostulationStates.CANCELLED)
                            .event(PostulationEvents.CANCEL)
                .and()
                .withExternal()
                    .source(PostulationStates.DOCUMENTS_VALIDATED)
                        .target(PostulationStates.CANCELLED)
                            .event(PostulationEvents.CANCEL);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PostulationStates, PostulationEvents> config) throws Exception {
        config.withConfiguration().listener(postulantListener());
    }

    @Bean
    public StateMachineListener<PostulationStates, PostulationEvents> postulantListener() {
        return new StateMachineListenerAdapter<PostulationStates, PostulationEvents>(){
            @Override
            public void transition(Transition<PostulationStates, PostulationEvents> transition){
                System.out.println("LISTENER...");
                if(transition!=null && transition.getSource()!=null && transition.getSource().getId()!=null){
                    System.out.println("Transitioning form "+ transition.getSource().getId()
                            +" to "+transition.getTarget().getId());
                }
            };
        };
    }
//-----------------------------------------ACTIONS----------------------------------------------------------------------
    @Bean
    public Action<PostulationStates, PostulationEvents> validatePersonAction(){
        return context ->{
            System.out.println("Init action validatePersonAction...");
            Integer cedula = (Integer)context.getMessageHeader("cedula");
            Person person = new Person();
            person.setCedula(cedula);
            BodyInserter<Person, ReactiveHttpOutputMessage> selectPerson = BodyInserters.fromValue(person);
            Flux<Person> personFlux = webClient.post()
                    .uri("http://localhost:8082/humanresources/person/select")
                    .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                    .body(selectPerson)
                    .retrieve()
                    .bodyToFlux(Person.class);

            personFlux
                    .doOnComplete(()->{
                        System.out.println("PROFESSION1: "+profession);
                        postulantTrigger.validateProfession(Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PROFESSION)
                                        .setHeader("profession",profession)
                                        .build()));
                    })
                    .subscribe(result->{
                        investigation = new Investigation();
                        investigation.setPerson(result);
                        profession = result.getProfession();
                    });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateProfessionAction() {
        return context ->{
            BodyInserter<Profession, ReactiveHttpOutputMessage> selectProfession = BodyInserters.fromValue(profession);
            Flux<Profession> professionFlux = webClient.post()
                    .uri("http://localhost:8082/humanresources/profession/select")
                    .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                    .body(selectProfession)
                    .retrieve()
                    .bodyToFlux(Profession.class);
            System.out.println("Init action validateProfessionAction...");
            System.out.println(context.getStateMachine().getState().getId());
            Profession profession = (Profession)context.getMessageHeader("profession");
            System.out.println("PROFESSION2: "+profession);

           professionFlux.
            doOnComplete(()->{
                System.out.println("INVESTIGATION_PERSON: "+investigation.getPerson().getCedula());
                postulantTrigger.validateCriminalRecords(Mono.just(
                        MessageBuilder
                                .withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                                .setHeader("person",investigation.getPerson())
                                .build()
                ));
            System.out.println("Profession validate Action");
        }).subscribe(result->{
               if(result.getProfessionId().equals(profession.getProfessionId())){
                   System.out.println("SON IGUALES");
               }
           });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> criminalRecordsAction() {
        return context ->{
            System.out.println("Init action criminalRecordsAction...");
            Person person = (Person)context.getMessageHeader("person");
            BodyInserter<Person, ReactiveHttpOutputMessage> selectPerson = BodyInserters.fromValue(person);
            Flux<CriminalRecord> criminalRecordFlux = webClient.post()
                    .uri("http://localhost:8082/humanresources/criminalrecord/select")
                    .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                    .body(selectPerson)
                    .retrieve()
                    .bodyToFlux(CriminalRecord.class);
            List<CriminalRecord> criminalRecordList = new ArrayList<>();
            criminalRecordFlux
                    .doOnComplete(()->{
                        investigation.setCriminalRecords(criminalRecordList);
                        criminalRecordList.forEach(crr->{
                            System.out.println("CRR: "+crr.getCriminalRecordDescription());
                        });
                        investigation.getCriminalRecords().forEach(cr2->{
                            System.out.println("CR2: "+cr2.getCriminalRecordDescription());
                        });

                        postulantTrigger.validateQualification(Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.VALIDATE_QUALIFICATION)
                                        .setHeader("person",investigation.getPerson())
                                        .build()));
                    })
                    .subscribe(result ->{
                        criminalRecordList.add(result);
                        System.out.println("CRIMINAL_RECORD: "+result.getCriminalRecordDescription());
                    });
        };
    }


    @Bean
    public Action<PostulationStates, PostulationEvents> validateQualificationAction() {
        return context ->{
            System.out.println("Init action validateQualificationAction...");
            var isValid = false;
            Person person = (Person)context.getMessageHeader("person");
            BodyInserter<Person, ReactiveHttpOutputMessage> selectPerson = BodyInserters.fromValue(person);

            Flux<Qualification> qualificationFlux = webClient.post()
                    .uri("http://localhost:8082/humanresources/qualification/select")
                    .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                    .body(selectPerson)
                    .retrieve()
                    .bodyToFlux(Qualification.class);
            qualificationFlux.doOnComplete(()->{
                postulantTrigger.validateDocument(Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.VALIDATE_DOCUMENTS)
                                .setHeader("document", document)
                                .build()
                ));
            }).subscribe( result ->{
                document = result.getDocument();
                System.out.println("QUALIFICATION_DOCUMENT_IS_APPROVED: "+result.getDocument().isDocumentApproved());
                System.out.println("QUALIFICATION_DOCUMENT: "+result.getDocument());
                System.out.println("IS_APPROVED:"+result.isApproved());

            });
            System.out.println("IS_VALID: "+isValid);
            System.out.println("Document validate Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> obtainDocumentAction() {
        return context ->{
            System.out.println("Init action obtainDocumentAction...");
            var isValid = false;
            postulantTrigger.validateCriminalRecords(Mono.just(
                    MessageBuilder.withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                            .build()
            ));
            System.out.println("IS_VALID: "+isValid);
            System.out.println("Document Obtained Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateDocumentAction() {
        return context ->{
            Document document = (Document)context.getMessageHeader("document");
            BodyInserter<Document, ReactiveHttpOutputMessage> selectDocument= BodyInserters.fromValue(document);
            Flux<Document> documentFlux = webClient.post()
                    .uri("http://localhost:8082/humanresources/document/select")
                    .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                    .body(selectDocument)
                    .retrieve()
                    .bodyToFlux(Document.class);
            System.out.println("Init action validateDocumentAction...");
            documentFlux.doOnComplete(()->{
                postulantTrigger.validateCompletedAction(Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.COMPLETE)
                                .build()
                ));
            }).subscribe(result ->{
                var isValid = result.isDocumentApproved();
                System.out.println("IS_VALID: "+isValid);
            });

            System.out.println("Document validate Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> obtainInstituteAction() {
        return context ->{
            System.out.println("Init action obtainInstituteAction...");
            var isValid = false;
            postulantTrigger.validateCriminalRecords(Mono.just(
                    MessageBuilder.withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                            .build()
            ));
            System.out.println("IS_VALID: "+isValid);
            System.out.println("Document validate Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateInstituteAction() {
        return context ->{
            System.out.println("Init action validateInstituteAction...");
            var isValid = false;
            postulantTrigger.validateCriminalRecords(Mono.just(
                    MessageBuilder.withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                            .build()
            ));
            System.out.println("IS_VALID: "+isValid);
            System.out.println("Document validate Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateDocumentsAction() {
        return context ->{
            System.out.println("Init action validateDocumentsAction...");
            var isValid = false;
            postulantTrigger.validateCriminalRecords(Mono.just(
                    MessageBuilder.withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                            .build()
            ));
            System.out.println("IS_VALID: "+isValid);
            System.out.println("Document validate Action: ");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> completedAction() {
        return context ->{
            System.out.println("Init action completedAction...");
            postulantTrigger.stopPostulationSaga();
            System.out.println("End action completeAction.");
        };
    }
    //----------------------------------------------GUARDS--------------------------------------------------------------

    @Bean
    public Guard<PostulationStates, PostulationEvents> guardValidatePersons() {
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                return false;
            }
        };
    }

    @Bean
    public Guard<PostulationStates, PostulationEvents> guardValidateDocuments() {
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {

                return false;
            }
        };
    }

    @Bean
    public ReactiveGuard<PostulationStates, PostulationEvents> guardValidateDocuments2() {


        return new ReactiveGuard<PostulationStates, PostulationEvents>() {
            @Override
            public Mono<Boolean> apply(StateContext<PostulationStates, PostulationEvents> postulationStatesPostulationEventsStateContext) {
                postulationStatesPostulationEventsStateContext.getMessageHeader("");
                return null;
            }
        };
    }
}