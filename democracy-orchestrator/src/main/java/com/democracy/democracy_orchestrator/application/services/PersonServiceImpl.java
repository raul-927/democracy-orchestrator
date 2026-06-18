package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.person.SelectPersonIn;
import com.democracy.democracy_orchestrator.domain.ports.in.person.UpdatePersonIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PersonServiceImpl implements PersonService{
    private final SelectPersonIn selectPersonIn;
    private final UpdatePersonIn updatePersonIn;

    public PersonServiceImpl(SelectPersonIn selectPersonIn, UpdatePersonIn updatePersonIn) {
        this.selectPersonIn = selectPersonIn;
        this.updatePersonIn = updatePersonIn;
    }

    @Override
    public Flux<Person> selectPerson(Person person) {
        return this.selectPersonIn.selectPerson(person);
    }

    @Override
    public Mono<Long> selectCount() {
        return selectPersonIn.selectCount();
    }

    @Override
    public Mono<Integer> updatePerson(Person person) {
        return this.updatePersonIn.updatePerson(person);
    }
}
