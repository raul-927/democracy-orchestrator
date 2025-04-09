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
    public boolean validatedTransition(Message message) {
        System.out.println("validatedTransition： " + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "VALIDATED", target = "PAID")
    public boolean paidTransition(Message message) {

        System.out.println("paidTransition： " + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "PAID", target = "SHIPPED")
    public boolean shippedTransition(Message message) {

        System.out.println("shippedTransition： " + message.getHeaders().toString());
        return true;
    }

    @OnTransition(source = "SHIPPED", target = "COMPLETED")
    public boolean completedTransition(Message message) {

        System.out.println("completedTransition： " + message.getHeaders().toString());
        return true;
    }

}