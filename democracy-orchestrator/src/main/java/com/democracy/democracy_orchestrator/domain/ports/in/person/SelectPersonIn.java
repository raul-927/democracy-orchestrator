package com.democracy.democracy_orchestrator.domain.ports.in.person;

import com.democracy.democracy_orchestrator.domain.models.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SelectPersonIn {
    Flux<Person> selectPerson(Person person);
}
