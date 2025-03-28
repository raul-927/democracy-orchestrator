package com.democracy.validatedepartment.application.services;

import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.models.Order;
import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.application.statemachine.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private StateMachineFactory<OrderStates, OrderEvents> machineFactory;
    private StateMachine<OrderStates, OrderEvents> stateMachine;

    //@Autowired
    //private TokenService obtainToken;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public Order newOrder(Order order) {
        initOrderSaga();
        validateOrder(order);
        payOrder();
        shipOrder();
        completeOrder();
        return order;
    }
    @Override
    public void initOrderSaga(){
        System.out.println("Initializing order saga");
        stateMachine = machineFactory.getStateMachine();
        stateMachine.startReactively().subscribe();
        System.out.println("Final state initOrderSaga: "+stateMachine.getState().getId());
    }
    @Override
    public void validateOrder(Order order) {
        List<Department> departments = departmentService.selectAllDepartment();
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.VALIDATE)
                                .setHeader("order",order)
                                .setHeader("departmentList", departments).build()))
                .subscribe(result -> System.out.println("RESULT validateOrder: "+result.getResultType()));
        System.out.println("Final state validateOrder: "+stateMachine.getState().getId());
    }
    @Override
    public void payOrder() {
        System.out.println("Paying order...");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.PAY).build()))
                .subscribe(result -> System.out.println("RESULT payOrder: "+result.getResultType()));
        System.out.println("Final state payOrder: "+stateMachine.getState().getId());
    }
    @Override
    public void shipOrder() {
        System.out.println("Shipping order...");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.SHIP).build()))
                .subscribe(result -> System.out.println("RESULT shipOrder: "+result.getResultType()));
        System.out.println("Final state: shipOrder "+stateMachine.getState().getId());
    }
    @Override
    public void completeOrder() {
        System.out.println("Completing order.");
        stateMachine.sendEvent(Mono.just(
                        MessageBuilder.withPayload(OrderEvents.COMPLETE).build()))
                .subscribe(result -> System.out.println("RESULT completeOrder: "+result.getResultType()));
        System.out.println("Final state completeOrder: "+stateMachine.getState().getId());
        stopOrderSaga();
    }
    @Override
    public void stopOrderSaga(){
        System.out.println("Stopping saga...");
        System.out.println("------------------------");
        stateMachine.stopReactively().subscribe();
    }
}