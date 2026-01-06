package com.democracy.democracy_orchestrator.infrastructure.web.handlers;


import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.events.PostulationEvents;
import com.democracy.democracy_orchestrator.infrastructure.statemachine.trigers.PostulantTriggerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

//@Component
//@Slf4j
public class PostulantHandler {

    //@Autowired
    private PostulantTriggerImpl postulantTrigger;

    public Mono<ServerResponse> selectPostulant(ServerRequest request){
        postulantTrigger.initPostulationSaga();
        var obtainPerson = request.bodyToMono(Person.class);
        Person sendPerson  = new Person();
        obtainPerson.map( str ->{
            sendPerson.setPersonId(str.getPersonId());
            sendPerson.setCedula(str.getCedula());
            sendPerson.setProfession(str.getProfession());
            return sendPerson;
        });
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(postulantTrigger.sendEvent("VALIDATE_PERSON", Mono.just(
                        MessageBuilder.withPayload(PostulationEvents.VALIDATE_PERSON)
                                .setHeader("cedula", sendPerson.getCedula())
                                .build())), Person.class);
    }
}
