package com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation;

import com.democracy.democracy_orchestrator.application.services.InvestigationService;
import com.democracy.democracy_orchestrator.domain.models.InvestigationResult;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.postulation.PostulationStates;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PostulantTriggerImpl implements PostulantTrigger {
    Logger LOGGER = LoggerFactory.getLogger(PostulantTriggerImpl.class);

    //@Autowired
    private StateMachineFactory<PostulationStates, PostulationEvents> orderStateMachineFactory;
    private StateMachine<PostulationStates, PostulationEvents> stateMachine;

    @Autowired
    private InvestigationService investigationService;

    @Override
    public void initPostulationSaga(){
        LOGGER.info("Initializing initPostulationSaga");
        stateMachine = orderStateMachineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
    }

    @Override
    public void stopPostulationSaga(){
        LOGGER.info("Initializing stopPostulationSaga");
        stateMachine.stopReactively().doOnSuccess(dos-> LOGGER.info("Final state stopPostulationSaga: {} {} ",dos,stateMachine.getState().getId())).subscribe();
        LOGGER.info("Stopping saga...");
    }


    @Override
    public void sendEvent(String eventDescription, Mono<Message<PostulationEvents>> event) {

       stateMachine.sendEvent(event)
                .subscribe(
                        result -> {
                            LOGGER.info("SEND_EVENT: {} {}",eventDescription+" Trigger: ",result.getResultType());

                        });
    }

    @Override
    public Mono<InvestigationResult> sendFinalEventInvestigationResult(String eventDescription, InvestigationResult investigationResult, Mono<Message<PostulationEvents>> event) {
        stateMachine.sendEvent(event)
                .subscribe(
                        result -> {
                            LOGGER.info("SEND_EVENT: {} {}",eventDescription+" Trigger: ",result.getResultType());

                        });
        return Mono.just(investigationResult);
    }
}