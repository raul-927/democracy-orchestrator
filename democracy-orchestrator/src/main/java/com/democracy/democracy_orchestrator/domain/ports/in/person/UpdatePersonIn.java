package com.democracy.democracy_orchestrator.domain.ports.in.person;

import com.democracy.democracy_orchestrator.domain.models.Person;
import reactor.core.publisher.Mono;

public interface UpdatePersonIn {
    Mono<Integer> updatePerson(Person person);
}
