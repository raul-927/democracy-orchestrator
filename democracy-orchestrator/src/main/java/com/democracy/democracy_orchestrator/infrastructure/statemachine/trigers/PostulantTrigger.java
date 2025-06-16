package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface PostulantTrigger {
    void initPostulationSaga();
    void stopPostulationSaga();
    void sendEvent(String eventDescription,Mono<Message<PostulationEvents>> event);
}
