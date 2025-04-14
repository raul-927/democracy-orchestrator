package com.democracy.validatedepartment.application.services;

import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.models.Order;
import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.application.statemachine.states.OrderStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private StateMachineFactory<OrderStates, OrderEvents> orderStateMachineFactory;
    private StateMachine<OrderStates, OrderEvents> stateMachine;

    //@Autowired
    //private TokenService obtainToken;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public Order newOrder(Order order) {
        initOrderSaga();
        validateOrder(order);
       // payOrder();
        //shipOrder();
        //completeOrder();
        return order;
    }
    @Override
    public void initOrderSaga(){
        System.out.println("Initializing order saga");
        stateMachine = orderStateMachineFactory.getStateMachine("123");
        stateMachine.startReactively().subscribe();
        System.out.println("Final state initOrderSaga: "+stateMachine.getState().getId());
    }
    @Override
    public void validateOrder(Order order) {
        //System.out.println("PROCESADORES: "+Runtime.getRuntime().availableProcessors());
        System.out.println("Validate order...");
        List<Department>departmentList = new ArrayList<>();
        Flux<Department> departments = departmentService.selectAllDepartment();
        departments
                .doOnComplete(()->{
                    System.out.println("Send event.... VALIDATE");
                    stateMachine.getExtendedState().getVariables().put("isComplete", true);
                    stateMachine.sendEvent(Mono.just(
                                    MessageBuilder.withPayload(OrderEvents.VALIDATE)
                                            .setHeader("order",order)
                                            .setHeader("departmentList", departments)
                                            .build()))
                            //.doOnComplete(this::payOrder)
                            .subscribe(result -> System.out.println("RESULT validateOrderService: "+result.getResultType()));
                }).subscribe();
    }
    @Override
    public void payOrder(Mono<Message<OrderEvents>> event) {
        System.out.println("Paying order...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT payOrder: "+result.getResultType()));
    }
    @Override
    public void shipOrder(Mono<Message<OrderEvents>> event) {
        System.out.println("Shipping order...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT shipOrder: "+result.getResultType()));

    }
    @Override
    public void completeOrder(Mono<Message<OrderEvents>> event) {
        System.out.println("Completing order...");
        stateMachine.sendEvent(event)
                .subscribe(result -> System.out.println("RESULT completeOrder: "+result.getResultType()));
    }
    @Override
    public void stopOrderSaga(){
        System.out.println("Stopping saga...");
        System.out.println("------------------------");
        stateMachine.stopReactively().subscribe();
    }
}