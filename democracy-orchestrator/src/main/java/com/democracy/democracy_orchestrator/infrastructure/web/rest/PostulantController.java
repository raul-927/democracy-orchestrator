package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.application.services.DepartmentService;
import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PostulantTriggerImpl postulantTrigger;


    @PostMapping("/investigation/select")
    public Mono<Integer> getInvestigation(@RequestBody Person person){
        System.out.println("PERSON_CEDULA: "+person.getCedula());
        postulantTrigger.initPostulationSaga();
        postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                        .setHeader("cedula", person.getCedula())
                        .build()));
        return Mono.just(person.getCedula());
    }
}
