package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.application.services.InvestigationService;
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
    private InvestigationService investigationService;

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;

    private Profession profession;

    private Document document;

    private Boolean isValidDocument;
    private Investigation investigation;

    @Override
    public void configure(StateMachineStateConfigurer<PostulationStates, PostulationEvents> states)throws Exception{
        states
                .withStates()
                .initial(PostulationStates.NEW)
                .choice(PostulationStates.DOCUMENTS_CHOICE)
                .end(PostulationStates.COMPLETED)
                .end(PostulationStates.CANCELLED)
                .states(EnumSet.allOf(PostulationStates.class));
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
                .and()
                .withExternal()
                    .source(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                        .target(PostulationStates.QUALIFICATION_VALIDATED)
                            .event(PostulationEvents.VALIDATE_QUALIFICATION)
                                .action(validateQualificationAction())
                .and()
                .withExternal()
                    .source(PostulationStates.QUALIFICATION_VALIDATED)
                        .target(PostulationStates.DOCUMENT_SEND_CHOICE)
                            .event(PostulationEvents.VALIDATE_DOCUMENTS)
                                .action(validateDocumentAction())
                .and()
                .withExternal()
                .source(PostulationStates.DOCUMENT_SEND_CHOICE)
                    .target(PostulationStates.DOCUMENTS_CHOICE)
                        .event(PostulationEvents.SEND_CHOICE)
                .and()
                .withChoice()
                    .source(PostulationStates.DOCUMENTS_CHOICE)
                        .first(PostulationStates.DOCUMENTS_VALIDATED, guardIsValidDocuments())
                            .last(PostulationStates.NOT_VALID)
                .and()
                .withExternal()
                    .source(PostulationStates.DOCUMENTS_VALIDATED)
                        .target(PostulationStates.COMPLETED)
                            //.event(PostulationEvents.COMPLETE)
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
                            .event(PostulationEvents.CANCEL)
                .and()
                .withExternal()
                    .source(PostulationStates.NOT_VALID)
                        .target(PostulationStates.COMPLETED)
                            .action(notValidAction());
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
                    System.out.println("Transitioning from "+ transition.getSource().getId()
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
                        postulantTrigger.validateProfession(Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PROFESSION)
                                        .setHeader("profession",profession)
                                        .build()));
                        profession = null;
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
           professionFlux.
            doOnComplete(()->{
                postulantTrigger.validateCriminalRecords(Mono.just(
                        MessageBuilder
                                .withPayload(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                                .setHeader("person",investigation.getPerson())
                                .build()
                ));
        }).subscribe();
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

                        });
                        investigation.getCriminalRecords().forEach(cr2->{

                        });

                        postulantTrigger.validateQualification(Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.VALIDATE_QUALIFICATION)
                                        .setHeader("person",investigation.getPerson())
                                        .build()));
                    })
                    .subscribe(criminalRecordList::add);
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
                List<Qualification> qualifications = new ArrayList<>();
                qualifications.add(result);
                investigation.setQualifications(qualifications);
                BodyInserter<Investigation, ReactiveHttpOutputMessage> insertInvestigation = BodyInserters.fromValue(investigation);
                Flux<Investigation> investigationFlux = webClient
                        .post()
                        .uri("http://localhost:8082/humanresources/investigation/insert")
                        .body(insertInvestigation)
                        .retrieve()
                        .bodyToFlux(Investigation.class);
                document = result.getDocument();
            });
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
                postulantTrigger.sendEvent("validateDocumentAction",Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.SEND_CHOICE)
                                .setHeader("isValidDocument",isValidDocument)
                                .build()));
            }).subscribe(result ->{
                isValidDocument = result.isDocumentApproved();
                System.out.println("DOCUMENT_IS_VALID: "+isValidDocument);
            });
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

    @Bean
    public Action<PostulationStates, PostulationEvents> notValidAction() {
        return context ->{
            System.out.println("Init action notValidAction...");
            postulantTrigger.stopPostulationSaga();
            System.out.println("End action notValidAction.");
        };
    }
    //----------------------------------------------GUARDS--------------------------------------------------------------


    @Bean
    public Guard<PostulationStates, PostulationEvents> guardIsValidDocuments() {
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean documentValid = (Boolean)context.getMessageHeader("isValidDocument");
                System.out.println("DOCUMENT_GUARD: "+documentValid);
                return documentValid;
            }
        };
    }
}