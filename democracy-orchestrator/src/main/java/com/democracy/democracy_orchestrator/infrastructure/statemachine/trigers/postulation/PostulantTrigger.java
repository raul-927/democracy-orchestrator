package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation;

import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.postulation.PostulationStates;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostulantTrigger {
    void initPostulationSaga();
    void stopPostulationSaga();
    Flux<StateMachineEventResult<PostulationStates, PostulationEvents>> sendEvent(String eventDescription, Mono<Message<PostulationEvents>> event);
    Mono<InvestigationResult> sendFinalEventInvestigationResult(String eventDescription,  InvestigationResult investigationResult, Mono<Message<PostulationEvents>> event);
}
