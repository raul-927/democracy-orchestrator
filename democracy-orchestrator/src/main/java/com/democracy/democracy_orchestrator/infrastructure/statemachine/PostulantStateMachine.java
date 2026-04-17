package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.application.services.*;
import com.democracy.democracy_orchestrator.domain.models.*;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.postulation.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTriggerImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents.*;
import static com.democracy.democracy_orchestrator.infrastructure.statemachine.states.postulation.PostulationStates.*;

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

    private Profession profession;
    private Document document;
    private Investigation investigation;
    private Person person;

    private Boolean isValidPerson;
    private Boolean isValidProfession;
    private Boolean isValidCriminalRecord;
    private Boolean isValidQualification;
    private Boolean isValidDocument;

    @Override
    public void configure(StateMachineStateConfigurer<PostulationStates, PostulationEvents> states)throws Exception{
        states
                .withStates()
                .initial(NEW)
                .choice(IS_VALIDATED_PERSON)
                .choice(IS_VALIDATE_PROFESSION)
                .choice(IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                .choice(IS_VALIDATE_RESULT_QUALIFICATIONS)
                .choice(IS_VALIDATED_DOCUMENTS)
                .end(COMPLETED)
                .end(CANCELLED)
                .end(ERROR)
                .states(EnumSet.allOf(PostulationStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PostulationStates, PostulationEvents> transitions)throws Exception{
        transitions
                .withExternal()
                    .source(NEW).target(PERSON_VALIDATED)
                    .event(VALIDATE_PERSON)
                    .action(validatePersonAction())

                .and()
                .withExternal()
                    .source(PERSON_VALIDATED).target(IS_VALIDATED_PERSON)
                    .event(SEND_RESULT_VALIDATED_PERSON)

                .and()
                .withChoice()
                    .source(IS_VALIDATED_PERSON)
                        .first(PROFESSION_VALIDATED, guardIsValidPerson())
                        .last(NOT_VALID)

                .and()
                .withExternal()
                    .source(PROFESSION_VALIDATED).target(RESULT_PROFESSION_VALIDATED)
                    .action(validateProfessionAction())

                .and()
                .withExternal()
                    .source(RESULT_PROFESSION_VALIDATED).target(IS_VALIDATE_PROFESSION)
                    .event(VALIDATE_PROFESSION)

                .and()
                .withChoice()
                    .source(IS_VALIDATE_PROFESSION)
                        .first(CRIMINAL_RECORDS_VALIDATED, guardIsValidProfession())
                        .last(NOT_VALID)

                .and()
                .withExternal()
                    .source(CRIMINAL_RECORDS_VALIDATED).target(RESULT_CRIMINAL_RECORDS_VALIDATED)
                    .action(validateCriminalRecordAction())

                .and()
                .withExternal()
                    .source(RESULT_CRIMINAL_RECORDS_VALIDATED).target(IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                    .event(SEND_RESULT_CRIMINAL_RECORD_VALIDATED)

                .and()
                .withChoice()
                    .source(IS_VALIDATED_RESULT_CRIMINAL_RECORD)
                        .first(QUALIFICATION_VALIDATED, guardIsValidatedResultCriminalRecord())
                        .last(NOT_VALID)

                .and()
                .withExternal()
                    .source(QUALIFICATION_VALIDATED).target(RESULT_VALIDATE_QUALIFICATIONS)
                    .action(validateQualificationAction())

                .and()
                .withExternal()
                    .source(RESULT_VALIDATE_QUALIFICATIONS).target(IS_VALIDATE_RESULT_QUALIFICATIONS)
                    .event(SEND_RESULT_VALIDATE_QUALIFICATIONS)

                .and()
                .withChoice()
                    .source(IS_VALIDATE_RESULT_QUALIFICATIONS)
                        .first(VALIDATE_DOCUMENT, guardIsValidateResultQualifications())
                        .last(NOT_VALID)

                .and()
                .withExternal()
                    .source(VALIDATE_DOCUMENT).target(RESULT_VALIDATE_DOCUMENT)
                    .action(validateDocumentAction())

                .and()
                .withExternal()
                    .source(RESULT_VALIDATE_DOCUMENT).target(IS_VALIDATED_DOCUMENTS)
                    .event(SEND_RESULT_VALIDATE_DOCUMENT)

                .and()
                .withChoice()
                    .source(IS_VALIDATED_DOCUMENTS)
                        .first(OBTAIN_RESULTS, guardIsDocumentResultValidated())
                        .last(NOT_VALID)

                .and()
                .withExternal()
                    .source(OBTAIN_RESULTS).target(COMPLETED)
                    .action(sendResultsInvestigationAction())

                .and()
                .withExternal()
                    .source(NOT_VALID).target(COMPLETED)
                    .action(completedAction())
                .and()
                .withExternal()
                    .source(OBTAIN_RESULTS).target(ERROR)
                    .event(SEND_ERROR)
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

            @Override
            public void stateEntered(State<PostulationStates, PostulationEvents> state) {
                super.stateEntered(state);
                LOGGER.info("State Entered: {}",state.getId());
            }
        };
    }
//-----------------------------------------ACTIONS----------------------------------------------------------------------
    @Bean
    public Action<PostulationStates, PostulationEvents> validatePersonAction(){
        return context ->{
            LOGGER.info("Init action validatePersonAction...");
            person = new Person();
            Integer cedula = (Integer) context.getMessageHeader("cedula");
            person.setCedula(cedula);
            personService.selectPerson(person)
                    .doOnComplete(()->{
                        LOGGER.info("End action validatePersonAction...");
                        postulantTrigger.sendEvent("SEND_RESULT_VALIDATED_PERSON",Mono.just(
                                MessageBuilder.withPayload(SEND_RESULT_VALIDATED_PERSON)
                                        .setHeader("isValidPerson",isValidPerson)
                                        .build()));
                        isValidPerson = false;

                    })
                    .subscribe(result->{
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
            professionService.selectProfession(profession).
                   doOnError(err->{
                       LOGGER.error("ERROR: {}", err.getMessage());
                   }).
            doOnComplete(()->{
                LOGGER.info("sendIsValidProfession: {}",isValidProfession);
                LOGGER.info("End action validateProfessionAction...");
                postulantTrigger.sendEvent("VALIDATE_PROFESSION ",Mono.just(
                        MessageBuilder
                                .withPayload(VALIDATE_PROFESSION)
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
            List<CriminalRecord> criminalRecordList = new ArrayList<>();
            criminalRecordService.selectCriminalRecord(cRecord)
                    .doOnComplete(()->{
                        LOGGER.info("End action validateCriminalRecordAction...");
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
            List<Qualification> qualifications = new ArrayList<>();
            qualificationService.selectQualification(qualification)
                    .doOnComplete(()->{
                        if(document ==null){
                            document = new Document();
                            document.setDocumentId("6ce735f8-f182-475a-bcd9-92a378bb6282");
                        }
                        investigation.setQualifications(qualifications);
                        postulantTrigger.sendEvent("SEND_RESULT_VALIDATE_QUALIFICATIONS",Mono.just(
                                MessageBuilder.withPayload(PostulationEvents.SEND_RESULT_VALIDATE_QUALIFICATIONS)
                                        .setHeader("obtainIsValidQualification", isValidQualification)
                                        .setHeader("document",document)
                                        .build()
                        ));
                    })
                    .switchIfEmpty( em->{
                        System.out.println("ESTA VACIO");
                    })
                    .subscribe( result ->{
                        isValidQualification = result.getApproved();
                        qualifications.add(result);
                        document = result.getDocument();
                    });
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateDocumentAction() {
        return context ->{
            LOGGER.info("Init action validateDocumentAction...");
            Document document = (Document)context.getMessageHeader("document");
            documentService.selectDocument(document)
                    .doOnComplete(()->{
                        LOGGER.info("End action validateDocumentAction...");
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
            String observation1 = "";
            investigation.setInvestigationId(UUID.randomUUID().toString());

            LOGGER.info("Init action sendResultsInvestigationAction...");
            Person updatePerson = new Person();
            updatePerson.setCedula(investigation.getPerson().getCedula());
            updatePerson.setIsProcessed(true);
            Mono<Integer> personResult = personService.updatePerson(updatePerson);
            investigationResultService.calculateScore(investigation)
                    .subscribe(result -> {
                LOGGER.info("End action sendResultsInvestigationAction... {}", result);
            });
            personResult.subscribe();
            postulantTrigger.stopPostulationSaga();
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
                return true;
            }
        };
    }

    @Bean
    public Guard<PostulationStates, PostulationEvents> guardIsValidateResultQualifications(){
        return new Guard<PostulationStates, PostulationEvents>() {

            @Override
            public boolean evaluate(StateContext<PostulationStates, PostulationEvents> context) {
                Boolean obtainIsValidQualifications = (Boolean)context.getMessageHeader("obtainIsValidQualification");
                if(obtainIsValidQualifications ==null){
                    obtainIsValidQualifications = true;
                }
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

                if(obtainIsValidDocument==null){
                    obtainIsValidDocument = false;
                }
                LOGGER.info("guardIsDocumentResultValidated: {}",obtainIsValidDocument);
                return true;
            }
        };
    }
}