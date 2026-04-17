package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ForkTrigger {

    void initForkSaga();
    void stopForkSaga();
    void sendEventFork(String eventDescription, Mono<Message<ForkJoinEvents>> event);
}
