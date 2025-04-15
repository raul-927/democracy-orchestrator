package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.domain.ports.out.PersonOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PersonAdapter implements PersonOut {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TokenService tokenService;


    @Override
    public Flux<Person> selectPerson(Person person) {
        BodyInserter<Person, ReactiveHttpOutputMessage> selectPerson = BodyInserters.fromValue(person);
        return webClient.post()
                .uri("http://localhost:8082/humanresources/person/select")
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectPerson)
                .retrieve()
                .bodyToFlux(Person.class);
    }
}
