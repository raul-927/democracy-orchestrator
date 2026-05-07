package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.forkjoin.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks.ForkTrigger;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostulantController.class);

    @Autowired
    private PostulantTrigger postulantTrigger;

    @Autowired
    private ForkTrigger forkTrigger;

    @Autowired
    private PersonService personService;


    @PostMapping(
            value = "/investigation/select",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  Flux<Person> getInvestigation(@RequestBody List<Person> person) {
        Flux<Person> personFlux = personService.selectPerson(new Person().setIsProcessed(false));
        personFlux
                .map(item->{
                    postulantTrigger.initPostulationSaga();
                    postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                            MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                                    .setHeader("cedula", item.getCedula())
                                    .build()));
                    return item;
                })

                .delayElements(Duration.ofMillis(300))
                .thenEmpty(em ->{
                    LOGGER.info("EMPTY personFlux");
                })
                .subscribe();
        return personFlux;
    }

    @PostMapping(
            value = "/investigation/fork",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getFork(@RequestBody List<Person> person) {

        Flux<Person> personFlux = personService.selectPerson(new Person().setIsProcessed(false));
        personFlux
                .doOnRequest(request->{
                    LOGGER.info("DO_ON_REQUEST...");
                    forkTrigger.initForkSaga();
                })
                .map(item->{
                    forkTrigger.sendEventFork("START_FORK", Mono.just(
                            MessageBuilder.withPayload(ForkJoinEvents.START_FORK)
                                    .setHeader("person", item)
                                    .build()));
                    return item;
                })
                .delayElements(Duration.ofMillis(300))
                .thenEmpty(em ->{
                    LOGGER.info("EMPTY personFlux");
                })
                .subscribe();
    }
}
