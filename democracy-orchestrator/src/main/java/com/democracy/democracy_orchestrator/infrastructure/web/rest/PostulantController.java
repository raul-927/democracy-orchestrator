package com.democracy.democracy_orchestrator.infrastructure.web.rest;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.forks.ForkTrigger;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTrigger;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTriggerImpl;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static com.democracy.democracy_orchestrator.infrastructure.statemachine.ForkJoinEvents.*;

@RestController
@RequestMapping("/democracyorchestrator")
public class PostulantController {

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
                    System.out.println("EMPTY personFlux");
                })
                .subscribe();
        return personFlux;
    }

    @PostMapping(
            value = "/investigation/fork",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getFork(@RequestBody List<Person> person) {

        forkTrigger.initForkSaga();
        forkTrigger.sendEventFork("START_FORK", Mono.just(
                MessageBuilder.withPayload(ForkJoinEvents.START_FORK).build()));

        //forkTrigger.stopForkSaga();

    }

    @PostMapping(
            value = "/investigation/branch1",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getBranch1(@RequestBody List<Person> person) {

        forkTrigger.sendEventFork("EVENT_BRANCH_1_COMPLETED", Mono.just(
                MessageBuilder.withPayload(ForkJoinEvents.EVENT_BRANCH_1_COMPLETED).build()));

        //forkTrigger.stopForkSaga();

    }

    @PostMapping(
            value = "/investigation/branch2",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getBranch2(@RequestBody List<Person> person) {

        forkTrigger.sendEventFork("EVENT_BRANCH_2_COMPLETED", Mono.just(
                MessageBuilder.withPayload(ForkJoinEvents.EVENT_BRANCH_2_COMPLETED).build()));

        //forkTrigger.stopForkSaga();

    }

    @PostMapping(
            value = "/investigation/branch3",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getBranch3(@RequestBody List<Person> person) {

        forkTrigger.sendEventFork("EVENT_BRANCH_3_COMPLETED", Mono.just(
                MessageBuilder.withPayload(ForkJoinEvents.EVENT_BRANCH_3_COMPLETED).build()));


    }

    @PostMapping(
            value = "/investigation/final",
            produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public  void getFinal(@RequestBody List<Person> person) {

        forkTrigger.sendEventFork("EVENT_FINAL", Mono.just(
                MessageBuilder.withPayload(EVENT_FINAL).build()));


    }
}
