package com.democracy.democracy_orchestrator.infrastructure.statemachine;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks.ForkTrigger;
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
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Collectors;

import static com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents.*;
import static com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinStates.*;

@Slf4j
@Configuration
@EnableStateMachineFactory(name = "forkJoinStateMachineFactory")
public class ForkJoinStateMachine extends EnumStateMachineConfigurerAdapter<ForkJoinStates, ForkJoinEvents> {

    @Autowired
    private ForkTrigger forkTrigger;

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
                .state(FINAL, finalAction(), null) // Corregido: La firma correcta para añadir acciones a un estado
                .end(FINAL)
                .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_1)
                    .end(BRANCH_1_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_2)
                    .end(BRANCH_2_DONE)
                    .and()
                .withStates()
                    .parent(FORK_POINT)
                    .initial(BRANCH_3)
                    .end(BRANCH_3_DONE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ForkJoinStates, ForkJoinEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(INITIAL)
                    .target(FORK_POINT)
                    .event(START_FORK)
                    .action(startAction())
                .and()
                .withFork()
                    .source(FORK_POINT)
                    .target(BRANCH_1)
                    .target(BRANCH_2)
                    .target(BRANCH_3)
                .and()
                .withExternal()
                    .source(BRANCH_1)
                    .target(BRANCH_1_DONE)
                    .event(EVENT_BRANCH_1_COMPLETED)
                    .action(branch1Action())
                .and()
                .withExternal()
                    .source(BRANCH_2)
                    .target(BRANCH_2_DONE)
                    .event(EVENT_BRANCH_2_COMPLETED)
                    .action(branch2Action())
                .and()
                .withExternal()
                    .source(BRANCH_3)
                    .target(BRANCH_3_DONE)
                    .event(EVENT_BRANCH_3_COMPLETED)
                    .action(branch3Action())
                .and()
                .withJoin()
                    .source(BRANCH_1_DONE)
                    .source(BRANCH_2_DONE)
                    .source(BRANCH_3_DONE)
                    .target(JOIN_POINT)
                .and()
                .withExternal()
                    .source(JOIN_POINT)
                    .target(FINAL);
    }

    @Bean
    public StateMachineListener<ForkJoinStates, ForkJoinEvents> forkJoinListener() {
        return new StateMachineListenerAdapter<ForkJoinStates, ForkJoinEvents>() {
            @Override
            public void transition(Transition<ForkJoinStates, ForkJoinEvents> transition) {
                if (transition != null && transition.getSource() != null && transition.getTarget() != null) {
                    LOGGER.info("Transition: {} -> {}", transition.getSource().getId(), transition.getTarget().getId());
                }
            }

            @Override
            public void stateChanged(State<ForkJoinStates, ForkJoinEvents> from, State<ForkJoinStates, ForkJoinEvents> to) {
                if (to != null) {
                    LOGGER.info("State changed to: {}", to.getId());
                    if (to.getIds() != null && !to.getIds().isEmpty()) {
                        LOGGER.info("Current active states (including regions): {}", to.getIds().stream().map(Enum::name).collect(Collectors.joining(", ")));
                    }
                }
            }
        };
    }
    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> startAction(){
        return context -> {
            LOGGER.info("startAction ejecutada.");

            Flux<Long> intervalNumbers = Flux.interval(Duration.ofSeconds(1))
                    .take(1);
            intervalNumbers
                    .doOnRequest(requested -> LOGGER.info("Requested with intervals: " + requested))
                    .doOnComplete(()->{
                        forkTrigger.sendEventFork("EVENT_BRANCH_1_COMPLETED", Mono.just(
                                MessageBuilder.withPayload(EVENT_BRANCH_1_COMPLETED).build()));
                        forkTrigger.sendEventFork("EVENT_BRANCH_2_COMPLETED", Mono.just(
                                MessageBuilder.withPayload(EVENT_BRANCH_2_COMPLETED).build()));
                        forkTrigger.sendEventFork("EVENT_BRANCH_3_COMPLETED", Mono.just(
                                MessageBuilder.withPayload(EVENT_BRANCH_3_COMPLETED).build()));
                    })
                    .subscribe();

        };
    }
    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch1Action() {
        return context -> {
            LOGGER.info("branch1Action ejecutada.");


        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch2Action() {
        return context -> {
            LOGGER.info("branch2Action ejecutada.");

        };
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> branch3Action() {
        return context -> LOGGER.info("branch3Action ejecutada.");
    }

    @Bean
    public Action<ForkJoinStates, ForkJoinEvents> finalAction() {
        return context -> {
            LOGGER.info("¡FINAL ACTION EJECUTADA! El proceso Fork/Join ha convergido correctamente.");
        };
    }
}
