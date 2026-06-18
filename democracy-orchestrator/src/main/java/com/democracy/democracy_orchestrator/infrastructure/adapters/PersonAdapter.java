package com.democracy.democracy_orchestrator.infrastructure.adapters;

import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.out.PersonOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.democracy.democracy_orchestrator.infrastructure.config.UrlConstant.*;

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
                .uri(LOCAL_HOST_8082 + HUMAN_RESOURCES + PERSON + SELECT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectPerson)
                .retrieve()
                .bodyToFlux(Person.class);
    }

    @Override
    public Mono<Integer> updatePerson(Person person) {
        BodyInserter<Person, ReactiveHttpOutputMessage> selectPerson = BodyInserters.fromValue(person);
        return webClient.put()
                .uri(LOCAL_HOST_8082 + HUMAN_RESOURCES + PERSON + UPDATE)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .body(selectPerson)
                .retrieve()
                .bodyToMono(Integer.class);
    }

    @Override
    public Mono<Long> selectCount() {
        return webClient.get()
                .uri(LOCAL_HOST_8082 + HUMAN_RESOURCES + PERSON + COUNT)
                .headers((headers) -> headers.add("authorization", tokenService.obtainToken()))
                .retrieve()
                .bodyToMono(Long.class);
    }
}
