package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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
        stateMachine.startReactively().subscribe();
    }

    @Override
    public void stopForkSaga() {
        LOGGER.info("Initializing stopForkSaga");
        stateMachine.stopReactively()
                .doOnSuccess(dos -> LOGGER.info("Final state stopForkSaga: {} {} ", dos, stateMachine.getState().getId()))
                .subscribe();
        LOGGER.info("Stopping fork saga...");
    }

    @Override
    public void sendEventFork(String eventDescription, Mono<Message<ForkJoinEvents>> event) {
        stateMachine.sendEvent(event)
                .subscribe(result -> {
                    LOGGER.info("SEND_EVENT_FORK: {} {}", eventDescription + " Trigger: ", result.getResultType());
                });
    }
}
