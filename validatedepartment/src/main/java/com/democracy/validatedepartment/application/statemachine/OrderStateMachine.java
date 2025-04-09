package com.democracy.validatedepartment.application.statemachine;




import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.models.Order;
import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.application.statemachine.states.OrderStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.List;

@Configuration
@EnableStateMachineFactory(name = "orderStateMachineFactory")
public class OrderStateMachine extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    private boolean isValid = false;
    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states)throws Exception{
        states
                .withStates()
                .initial(OrderStates.NEW)
                .states(EnumSet.allOf(OrderStates.class))
                .end(OrderStates.STATE_MACHINE_STOPED)
                .end(OrderStates.CANCELLED);

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions)throws Exception{
        transitions
                .withExternal()
                    .source(OrderStates.NEW)
                    .target(OrderStates.VALIDATED)
                    .event(OrderEvents.VALIDATE)
                    .action(validateOrderAction())
                    //.guard(guard1())
                .and()
                .withExternal()
                    .source(OrderStates.VALIDATED)
                    .target(OrderStates.PAID)
                    .event(OrderEvents.PAY)
                    .action(payOrderAction())
                .and()
                .withExternal()
                    .source(OrderStates.PAID)
                    .target(OrderStates.SHIPPED)
                    //.event(OrderEvents.SHIP)
                    .action(shipOrderAction())
                .and()
                .withExternal()
                    .source(OrderStates.SHIPPED)
                    .target(OrderStates.COMPLETED)
                    //.event(OrderEvents.COMPLETE)
                .action(completeOrder())
                .and()
                .withExternal()
                .source(OrderStates.COMPLETED)
                .target(OrderStates.STATE_MACHINE_STOPED)
                //.event(OrderEvents.STOP_STATE_MACHINE)
                .action(stopStateMachine())
                .and()
                .withExternal()
                    .source(OrderStates.VALIDATED)
                    .target(OrderStates.CANCELLED)
                    .event(OrderEvents.CANCEL)
                .and()
                .withExternal()
                    .source(OrderStates.PAID)
                    .target(OrderStates.CANCELLED)
                    .event(OrderEvents.CANCEL);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config) throws Exception {
        config.withConfiguration().listener(stateMachineListener());
    }
    @Bean
    public StateMachineListener<OrderStates, OrderEvents> stateMachineListener() {
        return new StateMachineListenerAdapter<OrderStates, OrderEvents>(){
            @Override
            public void transition(Transition<OrderStates, OrderEvents> transition){
                System.out.println("LISTENER...");
                if(transition!=null && transition.getSource()!=null && transition.getSource().getId()!=null){
                    System.out.println("Transitioning form "+ transition.getSource().getId()
                            +" to "+transition.getTarget().getId());
                }
            };
        };
    }
    //----------------------------ACTIONS-------------------------------------------------------------------------------
    @Bean
    public Action<OrderStates, OrderEvents> validateOrderAction() {
        return context ->{

            System.out.println("Init action validateOrderAction...");

            Flux<Department> departmentFlux = (Flux<Department>)context.getMessageHeader("departmentList");

            departmentFlux.doOnComplete(()->{
                System.out.println("ESTADO ACTUAL: "+context.getStateMachine().getState());
                System.out.println("Send event.... PAY");
                context.getExtendedState().getVariables().put("isComplete", true);
               context.getStateMachine().sendEvent(Mono.just(
                                MessageBuilder.withPayload(OrderEvents.PAY).build()))
                        .doOnComplete(()->System.out.println("Final Send Event PAY in validateOrderAction: "+context.getStateMachine().getState().getId()))
                        .subscribe(result -> System.out.println("RESULT send Event PAY in validateOrderAction: "+result.getResultType()));
            }).subscribe(dep->{
                System.out.println("DEPARTMENT_ID: "+dep.getDepartmentId());
                System.out.println("DEPARTMENT_NAME: "+dep.getDepartmentName());
            });
        };
    }

    @Bean
    public Action<OrderStates, OrderEvents> payOrderAction() {
        return context ->{
            System.out.println("Init action payOrderAction...");
            System.out.println("Send SHIP event...");
            /*context.getStateMachine().sendEvent(Mono.just(
                            MessageBuilder.withPayload(OrderEvents.SHIP).build()))
                    .doOnComplete(()->System.out.println("Final Send Event SHIP in payOrderAction: "+context.getStateMachine().getState().getId()))
                    .subscribe(result -> System.out.println("RESULT send Event SHIP in payOrderAction: "+result.getResultType()));*/
        };

    }

    @Bean
    public Action<OrderStates, OrderEvents> shipOrderAction() {
        return context ->{
            System.out.println("Init action shipOrderAction...");
            System.out.println("Send event.... COMPLETE");
            /*context.getStateMachine().sendEvent(Mono.just(
                            MessageBuilder.withPayload(OrderEvents.COMPLETE).build()))
                    .doOnComplete(()->System.out.println("Final Send Event COMPLETE in shipOrderAction: "+context.getStateMachine().getState().getId()))
                    .subscribe(result -> System.out.println("RESULT send Event COMPLETE in shipOrderAction: "+result.getResultType()));*/
        };
    }

    public  Action<OrderStates, OrderEvents> completeOrder(){
        return context ->{
            System.out.println("Init action completeOrder...");
            System.out.println("Send event.... STOP_STATE_MACHINE");
            /*context.getStateMachine().sendEvent(Mono.just(
                            MessageBuilder.withPayload(OrderEvents.STOP_STATE_MACHINE).build()))
                    .doOnComplete(()->System.out.println("Final Send Event STOP_STATE_MACHINE in completeOrder: "+context.getStateMachine().getState().getId()))
                    .subscribe(result -> System.out.println("RESULT send Event STOP_STATE_MACHINE in completeOrder: "+result.getResultType()));*/

        };
    }

    public  Action<OrderStates, OrderEvents> stopStateMachine(){
        return context ->{
            System.out.println("Stopping saga...");
            System.out.println("------------------------");
            context.getStateMachine().stopReactively().subscribe();

        };
    }

    //------------------GUARDS------------------------------------------------------------------------------------------

    @Bean
    public Guard<OrderStates, OrderEvents> guard1() {
        return new Guard<OrderStates, OrderEvents>() {

            @Override
            public boolean evaluate(StateContext<OrderStates, OrderEvents> context) {
                    System.out.println("Init guard1 validateOrderAction...");
                    Order order = (Order) context.getMessageHeader("order");
                return context.getStateMachine().getExtendedState().get("isComplete", Boolean.class);
            }
        };
    }
}
