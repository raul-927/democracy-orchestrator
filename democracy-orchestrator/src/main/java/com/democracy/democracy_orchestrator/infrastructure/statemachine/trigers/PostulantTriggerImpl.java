package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers;

import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostulantTriggerImpl implements PostulantTrigger{

    @Autowired
    private StateMachineFactory<PostulationStates, PostulationEvents> orderStateMachineFactory;
    private StateMachine<PostulationStates, PostulationEvents> stateMachine;

    @Autowired
    private InvestigationService investigationService;

    @Override
    public void initPostulationSaga(){
        System.out.println("Initializing initPostulationSaga");
        stateMachine = orderStateMachineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
        System.out.println("Final state initPostulationSaga: "+stateMachine.getState().getId());
    }

    @Override
    public void stopPostulationSaga(){
        System.out.println("Initializing stopPostulationSaga");
        System.out.println("Stopping saga...");
        System.out.println("------------------------");
        stateMachine.stopReactively().subscribe();
        System.out.println("Final state stopPostulationSaga: "+stateMachine.getState().getId());
    }

    @Override
    public void validatePerson(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate person...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validatePersonTrigger: "+result.getResultType()));
    }
    @Override
    public void validateProfession(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate professions...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validateProfessionTrigger: "+result.getResultType()));
    }

    @Override
    public void validateDocument(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate documents...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validateDocumentTrigger: "+result.getResultType()));
    }

    @Override
    public void validateCriminalRecords(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate criminalRecords...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validateCriminalRecordsTrigger: "+result.getResultType()));
    }

    @Override
    public void validateCompletedAction(Mono<Message<PostulationEvents>> event){
        System.out.println("Validate completeAction...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT validateCompleteActionTrigger: "+result.getResultType()));
    }



}
