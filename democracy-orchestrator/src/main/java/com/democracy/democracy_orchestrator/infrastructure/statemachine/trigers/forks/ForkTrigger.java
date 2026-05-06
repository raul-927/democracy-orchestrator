package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface ForkTrigger {

    void initForkSaga();
    void stopForkSaga();
    void sendEventFork(String eventDescription, Mono<Message<ForkJoinEvents>> event);
}
