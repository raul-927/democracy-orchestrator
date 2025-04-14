package com.democracy.validatedepartment.application.statemachine.listeners;


import com.democracy.validatedepartment.application.services.OrderService;
import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.application.statemachine.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnStateChanged;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * @description: state listener
 */
@Component
@WithStateMachine
//@Transactional
public class OrderStatusListener extends StateMachineListenerAdapter<OrderStates, OrderEvents> {

    @Autowired
    private OrderService orderService;

    //------------------------------Transition--------------------------------------------------------------------------

    @OnTransition(source = "NEW", target = "VALIDATED")
    public Mono<Boolean> newValidatedTransition(Message message) {
        System.out.println("newValidatedTransition： " + message.getHeaders().toString());
        return Mono.just(true);
    }

    @OnTransition(source = "VALIDATED", target = "PAID")
    public Mono<Boolean> validatedPaidTransition(Message message) {

        System.out.println("validatedPaidTransition： " + message.getHeaders().toString());
        return Mono.just(true);
    }

    @OnTransition(source = "PAID", target = "SHIPPED")
    public Mono<Boolean> paidShippedTransition(Message message) {

        System.out.println("paidShippedTransition： " + message.getHeaders().toString());
        return Mono.just(true);
    }

    @OnTransition(source = "SHIPPED", target = "COMPLETED")
    public Mono<Boolean> shippedCompletedTransition(Message message) {

        System.out.println("shippedCompletedTransition： " + message.getHeaders().toString());
        return Mono.just(true);
    }
//------------------------------------States----------------------------------------------------------------------------
    @OnStateChanged(source = "NEW", target = "VALIDATED")
    public Mono<Boolean> validateOnStateChanged(Message message) {

        System.out.println("validateOnStateChanged： " + message.getHeaders().toString());
        return Mono.just(true);
    }


    //------------------------------------------------------------------------------------------------------------
    @Override
    public void stateChanged(State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {

    }

    @Override
    public void stateEntered(State<OrderStates, OrderEvents> state) {
    }

    @Override
    public void stateExited(State<OrderStates, OrderEvents> state) {
    }

    @Override
    public void transition(Transition<OrderStates, OrderEvents> transition) {
    }

    @Override
    public void transitionStarted(Transition<OrderStates, OrderEvents> transition) {
    }

    @Override
    public void transitionEnded(Transition<OrderStates, OrderEvents> transition) {
    }

    @Override
    public void stateMachineStarted(StateMachine<OrderStates, OrderEvents> stateMachine) {
    }

    @Override
    public void stateMachineStopped(StateMachine<OrderStates, OrderEvents> stateMachine) {
    }

    @Override
    public void eventNotAccepted(Message<OrderEvents> event) {
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
    }

    @Override
    public void stateMachineError(StateMachine<OrderStates, OrderEvents> stateMachine, Exception exception) {
    }

    @Override
    public void stateContext(StateContext<OrderStates, OrderEvents> stateContext) {
    }

}