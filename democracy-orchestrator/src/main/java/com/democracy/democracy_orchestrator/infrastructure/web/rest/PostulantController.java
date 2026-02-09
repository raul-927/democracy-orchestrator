package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.states.PostulationStates;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.parameters.P;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    @Autowired
    private PostulantTriggerImpl postulantTrigger;

    @Autowired
    private PersonService personService;


    @PostMapping("/investigation/select")
    public Flux<Person> getInvestigation(@RequestBody List<Person> person){
        Flux<Person> personFlux = personService.selectPerson(new Person().setIsProcessed(false));
        personFlux
                .map(item->{
                    System.out.println("CEDULA: "+item.getCedula());
                    postulantTrigger.initPostulationSaga();
                    postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                            MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                                    .setHeader("cedula", item.getCedula())
                                    .build()));
                    return item;
                })
                .delayElements(Duration.ofSeconds(6))
                .subscribe();
        return personFlux;
    }
}
