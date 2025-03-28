package com.democracy.validatedepartment.application.statemachine.listeners;


import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: state listener
 */
@Component
@WithStateMachine
//@Transactional
public class OrderStatusListener {
    @OnTransition(source = "NEW", target = "VALIDATED")
    public boolean payTransition(Message message) {
        System.out.println("pay，feedback by statemachine：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public boolean deliverTransition(Message message) {

        System.out.println("deliver，feedback by statemachine：" + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public boolean receiveTransition(Message message) {

        System.out.println("receive，feedback by statemachine：" + message.getHeaders().toString());
        return true;
    }

}