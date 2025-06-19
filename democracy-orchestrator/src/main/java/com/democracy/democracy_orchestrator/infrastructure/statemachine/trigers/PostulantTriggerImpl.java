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
        System.out.println("Initialize state machine in state: "+stateMachine.getState().getId());
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
    public void sendEvent(String eventDescription, Mono<Message<PostulationEvents>> event) {
        System.out.println("Initialize sendEvent: "+eventDescription+"...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("SEND_EVENT: "+eventDescription+" Trigger: "+result.getResultType()));
    }











}