package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.forkjoin.ForkJoinStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ForkTriggerImpl implements ForkTrigger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForkTriggerImpl.class);

    @Autowired
    private StateMachineFactory<ForkJoinStates, ForkJoinEvents> forkStateMachineFactory;
    
    private StateMachine<ForkJoinStates, ForkJoinEvents> stateMachine;

    @Override
    public void initForkSaga() {
        LOGGER.info("Initializing initForkSaga");
        stateMachine = forkStateMachineFactory.getStateMachine();
        stateMachine.startReactively().block(); // Asegura que la máquina arranque antes de continuar
        LOGGER.info("StateMachine started successfully. Current state: {}", stateMachine.getState().getId());
    }

    @Override
    public void stopForkSaga() {
        LOGGER.info("Initializing stopForkSaga");
        if (stateMachine != null) {
            stateMachine.stopReactively()
                    .doOnSuccess(dos -> LOGGER.info("Final state stopForkSaga: {} ", stateMachine.getState().getId()))
                    .subscribe();
        }
    }

    @Override
    public void sendEventFork(String eventDescription, Mono<Message<ForkJoinEvents>> event) {
        if (stateMachine == null) {
            LOGGER.error("StateMachine not initialized. Call initForkSaga first.");
            return;
        }

        LOGGER.info("Attempting to send event '{}'. Current state BEFORE sending: {}", eventDescription, stateMachine.getState().getId());

        stateMachine.sendEvent(event)
                .subscribe(result -> {
                    LOGGER.info("SEND_EVENT_FORK: {} {}", eventDescription + " Trigger: ", result.getResultType());
                    if (result.getResultType().name().equals("DENIED")) {
                        LOGGER.warn("Event DENIED. Current state AFTER sending: {}", stateMachine.getState().getId());
                    } else if (result.getResultType().name().equals("ACCEPTED")) {
                        LOGGER.info("Event ACCEPTED. New state AFTER sending: {}", stateMachine.getState().getId());
                    }
                });
    }
}
