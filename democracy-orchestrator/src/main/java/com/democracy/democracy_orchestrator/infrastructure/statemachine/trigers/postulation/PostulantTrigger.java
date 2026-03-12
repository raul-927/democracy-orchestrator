package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface PostulantTrigger {
    void initPostulationSaga();
    void stopPostulationSaga();
    void sendEvent(String eventDescription, Mono<Message<PostulationEvents>> event);
    Mono<InvestigationResult> sendFinalEventInvestigationResult(String eventDescription,  InvestigationResult investigationResult, Mono<Message<PostulationEvents>> event);
}
