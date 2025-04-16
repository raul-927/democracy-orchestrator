package com.democracy.democracy_orchestrator.infrastructure.statemachine.listeners;


import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @description: state listener
 */
//@Component
//@WithStateMachine
//@Transactional
public class OrderStatusListener {
    //@Autowired
    private PostulantTrigger postulantTrigger;
    //@OnStateChanged(source = "PERSON_VALIDATED", target = "PROFESSION_VALIDATED")
    public boolean payTransition(Message message) {
        System.out.println("State Changed...：" + message.getHeaders().toString());
        postulantTrigger.validateDocument(Mono.just(
                MessageBuilder.withPayload(PostulationEvents.VALIDATE_DOCUMENTS)
                        .build()
        ));
        return true;
    }

   // @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public boolean deliverTransition(Message message) {

        System.out.println("deliver，feedback by statemachine：" + message.getHeaders().toString());
        return true;
    }

    //@OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public boolean receiveTransition(Message message) {

        System.out.println("receive，feedback by statemachine：" + message.getHeaders().toString());
        return true;
    }

}