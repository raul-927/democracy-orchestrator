package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers;

import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface PostulantTrigger {
    void initPostulationSaga();
    void stopPostulationSaga();
    void validatePerson(Mono<Message<PostulationEvents>> event);
    void validateProfession(Mono<Message<PostulationEvents>> event);
    void validateDocument(Mono<Message<PostulationEvents>> event);
    void validateCriminalRecords(Mono<Message<PostulationEvents>> event);
    void validateQualification(Mono<Message<PostulationEvents>> event);
    void validateCompletedAction(Mono<Message<PostulationEvents>> event);
}
