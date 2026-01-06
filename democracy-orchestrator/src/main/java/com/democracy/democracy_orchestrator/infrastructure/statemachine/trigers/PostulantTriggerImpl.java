package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers;

import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class PostulantTriggerImpl implements PostulantTrigger{
    Logger LOGGER = LoggerFactory.getLogger(PostulantTriggerImpl.class);

    @Autowired
    private StateMachineFactory<PostulationStates, PostulationEvents> orderStateMachineFactory;
    private StateMachine<PostulationStates, PostulationEvents> stateMachine;

    @Autowired
    private InvestigationService investigationService;

    @Override
    public void initPostulationSaga(){
        LOGGER.info("Initializing initPostulationSaga");
        stateMachine = orderStateMachineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
        LOGGER.info("Initialize state machine in state: {}", stateMachine.getState().getId());

    }

    @Override
    public void stopPostulationSaga(){
        LOGGER.info("Initializing stopPostulationSaga");
        stateMachine.stopReactively().subscribe();
        LOGGER.info("Final state stopPostulationSaga: {}",stateMachine.getState().getId());
        LOGGER.info("Stopping saga...");
    }

    @Override
    public Flux<StateMachineEventResult<PostulationStates, PostulationEvents>> sendEvent(String eventDescription, Mono<Message<PostulationEvents>> event) {
        LOGGER.info("Initialize sendEvent: {}",eventDescription+"...");
       stateMachine.sendEvent(event)
                .subscribe(
                        result -> {
                            LOGGER.info("SEND_EVENT: {} {}",eventDescription+" Trigger: ",result.getResultType());

                        });
        return stateMachine.sendEvent(event);
    }
}