package com.democracy.validatedepartment.application.statemachine;




import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.models.Order;
import com.democracy.validatedepartment.application.statemachine.events.OrderEvents;
import com.democracy.validatedepartment.application.statemachine.states.OrderStates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.EnumSet;
import java.util.List;

@Configuration
@EnableStateMachineFactory(name = "orderStateMachineFactory")
public class OrderStateMachine extends EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states)throws Exception{
        states
                .withStates()
                .initial(OrderStates.NEW)
                .states(EnumSet.allOf(OrderStates.class))
                .end(OrderStates.COMPLETED)
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

    @Bean
    public Action<OrderStates, OrderEvents> shipOrderAction() {
        return context ->{
            System.out.println("Shipping order Action");
        };
    }

    @Bean
    public Action<OrderStates, OrderEvents> payOrderAction() {
        return context ->{
            System.out.println("Paying order Action");
        };
    }

    @Bean
    public Action<OrderStates, OrderEvents> validateOrderAction() {
        return context ->{
           Order order = (Order) context.getMessageHeader("order");
            List<Department> departments = (List<Department>) context.getMessageHeader("departmentList");
            departments.forEach(department -> {
                System.out.println("DEPARTMENT_id IN validateOrderAction: "+department.getDepartmentId());
                System.out.println("DEPARTMENT_NAME IN validateOrderAction: "+department.getDepartmentName());
            });

            System.out.println("Validating order Action: "+order.getOrderId() + ", "+order.getOrderType() + ", "+order.getProduct().getProductId()+", "+order.getProduct().getProductName());
        };
    }
}
