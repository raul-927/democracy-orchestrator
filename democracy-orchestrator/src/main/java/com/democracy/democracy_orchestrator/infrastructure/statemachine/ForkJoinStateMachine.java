package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.application.services.*;
import com.democracy.democracy_orchestrator.domain.models.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents.*;
import static com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinStates.*;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "forkJoinStateMachineFactory")
public class ForkJoinStateMachine extends EnumStateMachineConfigurerAdapter<ForkJoinStates, ForkJoinEvents> {

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
    private InvestigationResultService resultService;

    private Investigation investigationResult;
    private Document document;
    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinStateMachine.class);

    @Override
    public void configure(StateMachineConfigurationConfigurer<ForkJoinStates, ForkJoinEvents> config) throws Exception {
        config.withConfiguration().listener(forkJoinListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ForkJoinStates, ForkJoinEvents> states) throws Exception {
        states
                .withStates()
                .initial(INITIAL)
                .fork(FORK_POINT)
                .join(JOIN_POINT)
                .state(FINAL, finalAction()) // La acción se ejecuta al entrar en FINAL
                .end(FINAL)
                .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_1)
                    .state(BRANCH_1, branch1Task(), null)
                    .end(BRANCH_1_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_2)
                    .state(BRANCH_2, branch2Task(), null)
                    .end(BRANCH_2_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_3)
                    .state(BRANCH_3, branch3Task(), null)
                    .end(BRANCH_3_DONE)
                .and()
                .withStates()
                .parent(FORK_POINT)
                .initial(BRANCH_4)
                .state(BRANCH_4, branch4Task(), null)
                .end(BRANCH_4_DONE)
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ForkJoinStates, ForkJoinEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(INITIAL).target(FORK_POINT).event(START_FORK).action(startForkAction())
                .and()
                .withFork()
                    .source(FORK_POINT)
                    .target(BRANCH_1)
                    .target(BRANCH_2)
                    .target(BRANCH_3)
                    .target(BRANCH_4)
                .and()
                .withExternal().source(BRANCH_1).target(BRANCH_1_DONE).event(EVENT_BRANCH_1_COMPLETED)
                .and()
                .withExternal().source(BRANCH_2).target(BRANCH_2_DONE).event(EVENT_BRANCH_2_COMPLETED)
                .and()
                .withExternal().source(BRANCH_3).target(BRANCH_3_DONE).event(EVENT_BRANCH_3_COMPLETED)
                .and()
                .withExternal().source(BRANCH_4).target(BRANCH_4_DONE).event(EVENT_BRANCH_4_COMPLETED)
                .and()
                .withJoin()
                    .source(BRANCH_1_DONE)
                    .source(BRANCH_2_DONE)
                    .source(BRANCH_3_DONE)
                    .source(BRANCH_4_DONE)
                    .target(JOIN_POINT)
                .and()
                .withExternal()
                    .source(JOIN_POINT).target(FINAL);
    }

    @Bean
    public StateMachineListener<ForkJoinStates, ForkJoinEvents> forkJoinListener() {
        return new StateMachineListenerAdapter<ForkJoinStates, ForkJoinEvents>() {
            @Override
            public void stateChanged(State<ForkJoinStates, ForkJoinEvents> from, State<ForkJoinStates, ForkJoinEvents> to) {
                if (to != null) {
                    LOGGER.info("STATE_CHANGED: {}. Active IDs: {}", to.getId(), to.getIds());
                }
            }
        };
    }


    @Bean
    public Action<ForkJoinStates, ForkJoinEvents>startForkAction(){
        return context->{
            investigationResult = new Investigation();
            document = new Document();
            LOGGER.info("Initialize investigationResult...");
        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch1Task() {
        return context -> {
            LOGGER.info("Executing branch1Task...");
            Person person = (Person)context.getMessageHeader("person");
            if (person != null) {
                investigationResult.setPerson(person);
                professionService.selectProfession(person.getProfession())
                    .doFinally(signalType -> {
                        LOGGER.info("Branch 1 task completed. Sending EVENT_BRANCH_1_COMPLETED.");
                        context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_1_COMPLETED).build())).subscribe();
                    })
                    .subscribe();
            } else {
                LOGGER.warn("Branch 1: Person not found in ExtendedState.");
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_1_COMPLETED).build())).subscribe();
            }
        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch2Task() {
        return context -> {
            LOGGER.info("Executing branch2Task...");
            Person person = (Person)context.getMessageHeader("person");
            if (person != null) {
                CriminalRecord cr = new CriminalRecord();
                cr.setPerson(person);

                criminalRecordService.selectCriminalRecord(cr)
                    .doFinally(signalType -> {
                        LOGGER.info("Branch 2 task completed. Sending EVENT_BRANCH_2_COMPLETED.");
                        context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_2_COMPLETED).build())).subscribe();
                    })
                    .subscribe();
            } else {
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_2_COMPLETED).build())).subscribe();
            }
        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch3Task() {
        return context -> {
            LOGGER.info("Executing branch3Task...");
            Person person = (Person)context.getMessageHeader("person");
            if (person != null) {
                Qualification qReq = new Qualification();
                qReq.setPerson(person);
                qualificationService.selectQualification(qReq)
                    .doFinally(signalType -> {
                        LOGGER.info("Branch 3 with person task completed. Sending EVENT_BRANCH_3_COMPLETED.");
                        context.getStateMachine()
                                .sendEvent(Mono.just(MessageBuilder
                                        .withPayload(EVENT_BRANCH_3_COMPLETED)
                                        .setHeader("document", document)
                                        .build()))
                                .subscribe();
                    })
                        .doOnNext(qualification -> {
                          document =   qualification.getDocument();
                        })
                    .subscribe();
            } else {
                LOGGER.info("Branch 3 with null person task completed. Sending EVENT_BRANCH_3_COMPLETED.");
                context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_3_COMPLETED).build())).subscribe();
            }
        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch4Task() {
        return context -> {
            LOGGER.info("Executing branch4Task...");
            documentService.selectDocument(document)
                    .doFinally(signalType -> {
                        LOGGER.info("Branch 4 with person task completed. Sending EVENT_BRANCH_4_COMPLETED.");
                        context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(EVENT_BRANCH_4_COMPLETED).build())).subscribe();
                    })
                    .subscribe();
        };
    }



    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> finalAction() {
        return context -> {
            LOGGER.info("¡PROCESO FORK/JOIN FINALIZADO CON ÉXITO!");
            investigationResult.setInvestigationId(UUID.randomUUID().toString());

            LOGGER.info("Init action sendResultsInvestigationAction...");
            Person updatePerson = new Person();
            updatePerson.setCedula(investigationResult.getPerson().getCedula());
            updatePerson.setIsProcessed(true);
            resultService.calculateScore(investigationResult)
                    .doOnSuccess(success->{
                        LOGGER.info("End action sendResultsInvestigationAction...");
                    })
                    .subscribe();
            personService.updatePerson(updatePerson).subscribe();
            context.getStateMachine().stopReactively();

        };
    }
}
