package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    @Autowired
    private PostulantTriggerImpl postulantTrigger;


    @PostMapping("/investigation/select")
    public Flux<StateMachineEventResult<PostulationStates, PostulationEvents>> getInvestigation(@RequestBody Person person){
        postulantTrigger.initPostulationSaga();
        Flux<StateMachineEventResult<PostulationStates, PostulationEvents>> aux = postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                        .setHeader("cedula", person.getCedula())
                        .build()));

        return postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                        .setHeader("cedula", person.getCedula())
                        .build()));
    }
}
