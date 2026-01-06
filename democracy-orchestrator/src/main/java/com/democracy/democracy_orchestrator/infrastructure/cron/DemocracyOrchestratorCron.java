package com.democracy.democracy_orchestrator.infrastructure.cron;

import com.democracy.democracy_orchestrator.application.services.DepartmentService;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

//@Component
public class DemocracyOrchestratorCron {

    //@Autowired
    private DepartmentService departmentService;

    //@Autowired
    private PostulantTriggerImpl postulantTrigger;

   // @Scheduled(fixedRate = 5000)
    public void executeDemocracyOrchestrator(){
        postulantTrigger.initPostulationSaga();
        postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                        .setHeader("cedula", 43639518)
                        .build()));
    }
}
