package com.democracy.democracy_orchestrator.infrastructure.cron;

import com.democracy.democracy_orchestrator.application.services.PersonService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.postulation.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.postulation.PostulantTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Component
public class DemocracyOrchestratorCron {

    @Autowired
    private PersonService personService;

    //@Autowired
    private PostulantTriggerImpl postulantTrigger;

    //@Scheduled(fixedRate = 30000)
    public void executeDemocracyOrchestratorCron(){
        System.out.println("executeDemocracyOrchestratorCron...");
        Flux<Person> personFlux = personService.selectPerson(new Person().setIsProcessed(false));
        personFlux
                .map(item->{
                    //postulantTrigger.initPostulationSaga();
                    postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                            MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                                    .setHeader("cedula", item.getCedula())
                                    .build()));
                    return item;
                })
                .subscribe();
    }
}
