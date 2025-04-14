package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers;

import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostulantTrigger {

    @Autowired
    private StateMachineFactory<PostulationStates, PostulationEvents> orderStateMachineFactory;
    private StateMachine<PostulationStates, PostulationEvents> stateMachine;

    @Autowired
    private InvestigationService investigationService;

    public void initOrderSaga(){
        System.out.println("Initializing order saga");
        stateMachine = orderStateMachineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
        System.out.println("Final state initOrderSaga: "+stateMachine.getState().getId());
    }

    public void stopOrderSaga(){
        System.out.println("Stopping saga...");
        System.out.println("------------------------");
        stateMachine.stopReactively().subscribe();
    }

    public Flux<Investigation> validateDocuments(Investigation investigation){
        System.out.println("Validate documents...");
        Flux<Investigation> investigationResult = investigationService.selectInvestigation(investigation);
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.VALIDATE_DOCUMENTS)
                                .setHeader("investigationResult", investigationResult)
                                .build()))
                .subscribe(result -> System.out.println("RESULT validateDocumentsTrigger: "+result.getResultType()));

        return investigationResult;
    }

    public Disposable validateProfession(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate professions...");
        return stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validateProfessionTrigger: "+result.getResultType()));
    }


}
