package com.democracy.validatedepartment.application.statemachine;


import com.democracy.validatedepartment.application.statemachine.events.PostulationEvents;
import com.democracy.validatedepartment.application.statemachine.states.PostulationStates;
import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.models.Order;
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
@EnableStateMachineFactory(name ="postulantStateMachineFactory")
public class PostulantStateMachine extends EnumStateMachineConfigurerAdapter<PostulationStates, PostulationEvents> {

    @Override
    public void configure(StateMachineStateConfigurer<PostulationStates, PostulationEvents> states)throws Exception{
        states
                .withStates()
                .initial(PostulationStates.NEW)
                .states(EnumSet.allOf(PostulationStates.class))
                .end(PostulationStates.COMPLETED)
                .end(PostulationStates.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PostulationStates, PostulationEvents> transitions)throws Exception{
        transitions
                .withExternal()
                    .source(PostulationStates.NEW)
                    .target(PostulationStates.DOCUMENTS_VALIDATED)
                    .event(PostulationEvents.VALIDATE_DOCUMENTS)
                    .action(validateDocumentsAction())
                .and()
                .withExternal()
                    .source(PostulationStates.DOCUMENTS_VALIDATED)
                    .target(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                    .event(PostulationEvents.VALIDATE_CRIMINAL_RECORDS)
                    .action(criminalRecordsAction())
                .and()
                .withExternal()
                    .source(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                    .target(PostulationStates.PROFESSION_VALIDATED)
                    .event(PostulationEvents.VALIDATE_PROFESSION)
                    .action(validateProfessionAction())
                .and()
                .withExternal()
                    .source(PostulationStates.PROFESSION_VALIDATED)
                    .target(PostulationStates.COMPLETED)
                    .event(PostulationEvents.COMPLETE)
                    .action(completedAction())
                .and()
                .withExternal()
                    .source(PostulationStates.DOCUMENTS_VALIDATED)
                    .target(PostulationStates.CANCELLED)
                    .event(PostulationEvents.CANCEL)
                .and()
                .withExternal()
                    .source(PostulationStates.CRIMINAL_RECORDS_VALIDATED)
                    .target(PostulationStates.CANCELLED)
                    .event(PostulationEvents.CANCEL)
                .and()
                .withExternal()
                    .source(PostulationStates.PROFESSION_VALIDATED)
                    .target(PostulationStates.CANCELLED)
                    .event(PostulationEvents.CANCEL);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PostulationStates, PostulationEvents> config) throws Exception {
        config.withConfiguration().listener(postulantListenner());
    }
    @Bean
    public StateMachineListener<PostulationStates, PostulationEvents> postulantListenner() {
        return new StateMachineListenerAdapter<PostulationStates, PostulationEvents>(){
            @Override
            public void transition(Transition<PostulationStates, PostulationEvents> transition){
                System.out.println("LISTENER...");
                if(transition!=null && transition.getSource()!=null && transition.getSource().getId()!=null){
                    System.out.println("Transitioning form "+ transition.getSource().getId()
                            +" to "+transition.getTarget().getId());
                }
            };
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateProfessionAction() {
        return context ->{
            System.out.println("Proffesion validate Action");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> criminalRecordsAction() {
        return context ->{
            System.out.println("Criminal record validate Action");
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> validateDocumentsAction() {
        return context ->{
           Order order = (Order) context.getMessageHeader("order");
            List<Department> departments = (List<Department>) context.getMessageHeader("departmentList");
            departments.forEach(department -> {
                System.out.println("DEPARTMENT_id IN validateOrderAction: "+department.getDepartmentId());
                System.out.println("DEPARTMENT_NAME IN validateOrderAction: "+department.getDepartmentName());
            });

            System.out.println("Document validate Action: "+order.getOrderId() + ", "+order.getOrderType() + ", "+order.getProduct().getProductId()+", "+order.getProduct().getProductName());
        };
    }

    @Bean
    public Action<PostulationStates, PostulationEvents> completedAction() {
        return context ->{
            System.out.println("Completed postulant Action");
        };
    }
}
