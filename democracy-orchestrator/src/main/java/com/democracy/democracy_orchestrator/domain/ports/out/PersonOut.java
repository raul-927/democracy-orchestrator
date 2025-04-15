package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonOut {
    Flux<Person> selectPerson(Person person);
}
