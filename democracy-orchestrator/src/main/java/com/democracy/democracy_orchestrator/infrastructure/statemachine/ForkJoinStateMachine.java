package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.actions.forkjoin.ForkJoinActions;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.forkjoin.ForkJoinStates;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents.*;
import static com.democracy.democracy_orchestrator.infrastructure.statemachine.states.forkjoin.ForkJoinStates.*;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "forkJoinStateMachineFactory")
public class ForkJoinStateMachine extends EnumStateMachineConfigurerAdapter<ForkJoinStates, ForkJoinEvents> {


    private static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinStateMachine.class);

    @Autowired
    private ForkJoinActions forkJoinActions;

    @Override
    public void configure(StateMachineConfigurationConfigurer<ForkJoinStates, ForkJoinEvents> config) throws Exception {
        config
                .withConfiguration()
                .listener(forkJoinActions.forkJoinListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ForkJoinStates, ForkJoinEvents> states) throws Exception {
        states
                .withStates()
                .initial(INITIAL)
                .fork(FORK_POINT)
                .join(JOIN_POINT)
                .state(FINAL, forkJoinActions.finalAction()) // La acción se ejecuta al entrar en FINAL
                .end(FINAL)
                .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_1)
                    .state(BRANCH_1, forkJoinActions.branch1Task(), null) //La tarea se ejecuta al iniciar el estado BRANCH_1
                    .end(BRANCH_1_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_2)
                    .state(BRANCH_2, forkJoinActions.branch2Task(), null) //La tarea se ejecuta al iniciar el estado BRANCH_2
                    .end(BRANCH_2_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_3)
                    .state(BRANCH_3, forkJoinActions.branch3Task(), null) //La tarea se ejecuta al iniciar el estado BRANCH_3
                    .end(BRANCH_3_DONE)
                .and()
                .withStates()
                .parent(FORK_POINT)
                .initial(BRANCH_4)
                .state(BRANCH_4, forkJoinActions.branch4Task(), null) //La tarea se ejecuta al iniciar el estado BRANCH_4
                .end(BRANCH_4_DONE)
        ;
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ForkJoinStates, ForkJoinEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(INITIAL).target(FORK_POINT).event(START_FORK).action(forkJoinActions.startForkAction())
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


}
