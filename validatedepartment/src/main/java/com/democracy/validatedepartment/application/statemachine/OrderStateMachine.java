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
                    .event(OrderEvents.SHIP)
                    .action(shipOrderAction())
                .and()
                .withExternal()
                    .source(OrderStates.SHIPPED)
                    .target(OrderStates.COMPLETED)
                    .event(OrderEvents.COMPLETE)
                .action(completeOrderAction())
                .and()
                .withExternal()
                .source(OrderStates.COMPLETED)
                .target(OrderStates.STATE_MACHINE_STOPED)
                .event(OrderEvents.STOP_STATE_MACHINE)
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
                    System.out.println("Transitioning from "+ transition.getSource().getId()
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
            departmentFlux.subscribe(dep->{
                System.out.println("department in validateOrderAction: "+dep.getDepartmentName());
            });
        };
    }

    @Bean
    public Action<OrderStates, OrderEvents> payOrderAction() {
        return context ->{
            System.out.println("Init action payOrderAction...");
            Flux<Department> departmentFlux = (Flux<Department>)context.getMessageHeader("departmentList");
            departmentFlux.subscribe(dep->{
                System.out.println("department in payOrderAction: "+dep.getDepartmentName());
            });
        };

    }

    @Bean
    public Action<OrderStates, OrderEvents> shipOrderAction() {
        return context ->{
            System.out.println("Init action shipOrderAction...");
            Flux<Department> departmentFlux = (Flux<Department>)context.getMessageHeader("departmentList");
            departmentFlux.subscribe(dep->{
                System.out.println("department in shipOrderAction: "+dep.getDepartmentName());
            });
        };
    }

    public  Action<OrderStates, OrderEvents> completeOrderAction(){
        return context ->{
            System.out.println("Init action completeOrderAction...");
            Flux<Department> departmentFlux = (Flux<Department>)context.getMessageHeader("departmentList");
            departmentFlux.subscribe(dep->{
                System.out.println("department in completeOrderAction: "+dep.getDepartmentName());
            });

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
