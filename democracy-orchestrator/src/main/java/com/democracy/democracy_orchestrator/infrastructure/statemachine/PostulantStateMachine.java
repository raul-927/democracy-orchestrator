package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.application.services.*;
import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@EnableStateMachineFactory(name ="postulantStateMachineFactory")
public class PostulantStateMachine extends EnumStateMachineConfigurerAdapter<PostulationStates, PostulationEvents> {

    Logger LOGGER = LoggerFactory.getLogger(PostulantStateMachine.class);

    @Autowired
    private PostulantTriggerImpl postulantTrigger;

    @Autowired
    private PersonService personService;

    @Autowired
    private ProfessionService professionService;

    @Autowired
    private CriminalRecordService criminalRecordService;

    @Autowired
    private QualificationService qualificationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private InvestigationResultService investigationResultService;

    @Autowired
    private TokenService tokenService;

    private Profession profession;
    private Document document;
    private Investigation investigation;

    private Boolean isValidPerson;
    private Boolean isValidProfession;
    private Boolean isValidCriminalRecord;
    private Boolean isValidQualification;
    private Boolean isValidDocument;


    @Override
    public void configure(StateMachineStateConfigurer<PostulationStates, PostulationEvents> states)throws Exception{
        states
                .withStates()
                .initial(PostulationStates.NEW)
                .choice(PostulationStates.IS_VALIDATED_PERSON)
                .choice(PostulationStates.IS_VALIDATE_PROFESSION)
                .choice(PostulationStates.IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                .choice(PostulationStates.IS_VALIDATE_RESULT_QUALIFICATIONS)
                .choice(PostulationStates.IS_VALIDATED_DOCUMENTS)
                .end(PostulationStates.COMPLETED)
                .end(PostulationStates.CANCELLED)
                .end(PostulationStates.ERROR)
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
                        .target(PostulationStates.IS_VALIDATED_PERSON)
                            .event(PostulationEvents.SEND_RESULT_VALIDATED_PERSON)

                .and()
                .withChoice()
                    .source(PostulationStates.IS_VALIDATED_PERSON)
                    .first(PostulationStates.PROFESSION_VALIDATED, guardIsValidPerson())
                    .last(PostulationStates.NOT_VALID)

                .and()
                .withExternal()
                    .source(PostulationStates.PROFESSION_VALIDATED)
                        .target(PostulationStates.RESULT_PROFESSION_VALIDATED)
                                .action(validateProfessionAction())

                .and()
                .withExternal()
                    .source(PostulationStates.RESULT_PROFESSION_VALIDATED)
                        .target(PostulationStates.IS_VALIDATE_PROFESSION)
                            .event(PostulationEvents.VALIDATE_PROFESSION)

                .and()
                .withChoice()
                    .source(PostulationStates.IS_VALIDATE_PROFESSION)
                    .first(PostulationStates.CRIMINAL_RECORDS_VALIDATED, guardIsValidProfession())
                    .last(PostulationStates.NOT_VALID)

                .and()
                .withExternal()
                    .source(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                        .target(PostulationStates.RESULT_CRIMINAL_RECORDS_VALIDATED)
                                .action(validateCriminalRecordAction())

                .and()
                .withExternal()
                    .source(PostulationStates.RESULT_CRIMINAL_RECORDS_VALIDATED)
                        .target(PostulationStates.IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                            .event(PostulationEvents.SEND_RESULT_CRIMINAL_RECORD_VALIDATED)

                .and()
                .withChoice()
                    .source(PostulationStates.IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                    .first(PostulationStates.QUALIFICATION_VALIDATED, guardIsValidatedResultCriminalRecord())
                    .last(PostulationStates.NOT_VALID)

                .and()
                .withExternal()
                    .source(PostulationStates.QUALIFICATION_VALIDATED)
                        .target(PostulationStates.RESULT_VALIDATE_QUALIFICATIONS)
                                .action(validateQualificationAction())

                .and()
                .withExternal()
                    .source(PostulationStates.RESULT_VALIDATE_QUALIFICATIONS)
                        .target(PostulationStates.IS_VALIDATE_RESULT_QUALIFICATIONS)
                            .event(PostulationEvents.SEND_RESULT_VALIDATE_QUALIFICATIONS)

                .and()
                .withChoice()
                    .source(PostulationStates.IS_VALIDATE_RESULT_QUALIFICATIONS)
                    .first(PostulationStates.VALIDATE_DOCUMENT, guardIsValidateResultQualifications())
                    .last(PostulationStates.NOT_VALID)

                .and()
                .withExternal()
                    .source(PostulationStates.VALIDATE_DOCUMENT)
                        .target(PostulationStates.RESULT_VALIDATE_DOCUMENT)
                                .action(validateDocumentAction())

                .and()
                .withExternal()
                    .source(PostulationStates.RESULT_VALIDATE_DOCUMENT)
                        .target(PostulationStates.IS_VALIDATED_DOCUMENTS)
                            .event(PostulationEvents.SEND_RESULT_VALIDATE_DOCUMENT)

                .and()
                .withChoice()
                    .source(PostulationStates.IS_VALIDATED_DOCUMENTS)
                    .first(PostulationStates.OBTAIN_RESULTS, guardIsDocumentResultValidated())
                    .last(PostulationStates.NOT_VALID)

                .and()
                .withExternal()
                    .source(PostulationStates.OBTAIN_RESULTS)
                        .target(PostulationStates.COMPLETED)
                            .action(sendResultsInvestigationAction())

                .and()
                .withExternal()
                    .source(PostulationStates.NOT_VALID)
                        .target(PostulationStates.COMPLETED)
                            .action(completedAction())
                .and()
                .withExternal()
                .source(PostulationStates.OBTAIN_RESULTS)
                .target(PostulationStates.ERROR)
                .event(PostulationEvents.SEND_ERROR)
                .action(completedAction());
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
                LOGGER.info("LISTENER...");
                if(transition!=null && transition.getSource()!=null && transition.getSource().getId()!=null){
                    LOGGER.info("Transitioning from: {}, to: {}",transition.getSource().getId(), transition.getTarget().getId());
                }
            };
        };
    }
//-----------------------------------------ACTIONS----------------------------------------------------------------------
    @Bean
    public Action<PostulationStates, PostulationEvents> validatePersonAction(){
        return context ->{
            LOGGER.info("Init action validatePersonAction...");
            Integer cedula = (Integer)context.getMessageHeader("cedula");
            System.out.println("CEDULA: "+cedula);
            Person person = new Person();
            person.setCedula(cedula);
            Flux<Person> personFlux = personService.selectPerson(person);
            personFlux
                    .doOnComplete(()->{
                        postulantTrigger.sendEvent("SEND_RESULT_VALIDATED_PERSON",Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.SEND_RESULT_VALIDATED_PERSON)
                                        .setHeader("isValidPerson",isValidPerson)
                                        .build()));
                        isValidPerson = false;

                    })
                    .subscribe(result->{
                        System.out.println("CEDULA-FLUX. "+result.getCedula());
                        profession = result.getProfession();
                        isValidPerson= result.getPersonId() != null;
                        investigation = new Investigation();
                        investigation.setPerson(result);
                    });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateProfessionAction() {
        return context ->{
            LOGGER.info("Init action validateProfessionAction...");
            Flux<Profession> professionFlux = professionService.selectProfession(profession);
           professionFlux.
                   doOnError(err->{
                       LOGGER.error("ERROR: {}", err.getMessage());
                   }).
            doOnComplete(()->{
                postulantTrigger.sendEvent("VALIDATE_PROFESSION ",Mono.just(
                        MessageBuilder
                                .withPayload(PostulationEvents.VALIDATE_PROFESSION)
                                .setHeader("sendIsValidProfession",isValidProfession)
                                .build()
                ));
            }).subscribe( result ->{
                       isValidProfession = result.getProfessionId() !=null;
                       investigation.getPerson().setProfession(result);
                });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateCriminalRecordAction() {
        return context ->{
            LOGGER.info("Init action validateCriminalRecordAction...");
            CriminalRecord cRecord = new CriminalRecord();
            cRecord.setPerson(investigation.getPerson());
            Flux<CriminalRecord> criminalRecordFlux = criminalRecordService.selectCriminalRecord(cRecord);
            List<CriminalRecord> criminalRecordList = new ArrayList<>();
            criminalRecordFlux
                    .doOnComplete(()->{
                        investigation.setCriminalRecords(criminalRecordList);
                        isValidCriminalRecord = !criminalRecordList.isEmpty();
                        postulantTrigger.sendEvent("SEND_RESULT_CRIMINAL_RECORD_VALIDATED",Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.SEND_RESULT_CRIMINAL_RECORD_VALIDATED)
                                        .setHeader("sendResultIsValidCriminalRecord",isValidCriminalRecord)
                                        .setHeader("person",investigation.getPerson())
                                        .build()));
                    })
                    .subscribe(
                            criminalRecordList::add);
        };
    }
    @Bean
    public Action<PostulationStates, PostulationEvents> validateQualificationAction() {
        return context ->{
            LOGGER.info("Init action validateQualificationAction...");
            Person person = (Person)context.getMessageHeader("person");
            Qualification qualification = new Qualification();
            qualification.setPerson(person);
            Flux<Qualification> qualificationFlux = qualificationService.selectQualification(qualification);
            qualificationFlux.doOnComplete(()->{
                postulantTrigger.sendEvent("SEND_RESULT_VALIDATE_QUALIFICATIONS",Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.SEND_RESULT_VALIDATE_QUALIFICATIONS)
                                .setHeader("obtainIsValidQualification", isValidQualification)
                                .setHeader("document",document)
                                .build()
                ));
            }).subscribe( result ->{
                isValidQualification = result.isApproved();
                List<Qualification> qualifications = new ArrayList<>();
                qualifications.add(result);
                investigation.setQualifications(qualifications);
                document = result.getDocument();
            });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateDocumentAction() {
        return context ->{
            LOGGER.info("Init action validateDocumentAction...");
            Document document = (Document)context.getMessageHeader("document");
            Flux<Document> documentFlux = documentService.selectDocument(document);
            documentFlux.doOnComplete(()->{
                postulantTrigger.sendEvent("SEND_RESULT_VALIDATE_DOCUMENT",Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.SEND_RESULT_VALIDATE_DOCUMENT)
                                .setHeader("isValidDocument",isValidDocument)
                                .build()));
            }).subscribe(result ->{
                LOGGER.info("DOCUMENT_ID: {}",result.getDocumentId());
                isValidDocument = result.isDocumentApproved();
                investigation.getQualifications().forEach(qualification->{
                    qualification.setDocument(result);
                });
            });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> completedAction() {
        return context ->{
            LOGGER.info("Init action completedAction...");
            postulantTrigger.stopPostulationSaga();
            LOGGER.info("End action completeAction.");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> calculateScoreAction(){
        return context -> {

        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> sendResultsInvestigationAction() {
        return context -> {
            investigation.setInvestigationId(UUID.randomUUID().toString());
            investigation.setObservation("Observación de prueba. Se investiga y se obtiene que existen registro de antecedentes delictivos");
            LOGGER.info("Init action sendResultsInvestigationAction...");
            Mono<Integer> investigationResultMono = investigationResultService.calculateScore(investigation);
            postulantTrigger.stopPostulationSaga();
            investigationResultMono.subscribe(result -> {
                LOGGER.info("End action sendResultsInvestigationAction... {}", result);
            });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> notValidAction() {
        return context ->{
            LOGGER.info("Init action notValidAction...");
            postulantTrigger.stopPostulationSaga();
            LOGGER.info("End action notValidAction.");
        };
    }

    //----------------------------------------------GUARDS--------------------------------------------------------------
    @Bean
    public Guard<PostulationStates, PostulationEvents> guardIsValidPerson(){

        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean isPostulantValid = (Boolean)context.getMessageHeader("isValidPerson");
                LOGGER.info("guardIsValidPerson: {}",isPostulantValid);
                return isPostulantValid;
            }
        };
    }

    @Bean
    public Guard<PostulationStates, PostulationEvents>guardIsValidProfession(){
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean isValidProfession = (Boolean)context.getMessageHeader("sendIsValidProfession");
                LOGGER.info("guardIsValidProfession: {}",isValidProfession);
                return isValidProfession;
            }
        };
    }

    @Bean
    public Guard<PostulationStates,PostulationEvents>guardIsValidatedResultCriminalRecord(){
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean isValidResultCriminalRecord = (Boolean)context.getMessageHeader("sendResultIsValidCriminalRecord");
                LOGGER.info("guardIsValidatedResultCriminalRecord: {}",isValidResultCriminalRecord);
                return isValidResultCriminalRecord;
            }
        };
    }

    @Bean
    public Guard<PostulationStates, PostulationEvents> guardIsValidateResultQualifications(){
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean obtainIsValidQualifications = (Boolean)context.getMessageHeader("obtainIsValidQualification");
                LOGGER.info("guardIsValidateResultQualifications: {}",obtainIsValidQualifications);
                return obtainIsValidQualifications;
            }
        };
    }

    @Bean
    public Guard<PostulationStates, PostulationEvents> guardIsDocumentResultValidated(){
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean obtainIsValidDocument = (Boolean)context.getMessageHeader("isValidDocument");
                LOGGER.info("guardIsDocumentResultValidated: {}",obtainIsValidDocument);
                return obtainIsValidDocument;
            }
        };
    }
}