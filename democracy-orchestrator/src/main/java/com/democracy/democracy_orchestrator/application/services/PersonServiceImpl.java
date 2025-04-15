package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.person.SelectPersonIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PersonServiceImpl implements PersonService{
    private final SelectPersonIn selectPersonIn;

    public PersonServiceImpl(SelectPersonIn selectPersonIn) {
        this.selectPersonIn = selectPersonIn;
    }

    @Override
    public Flux<Person> selectPerson(Person person) {
        return this.selectPersonIn.selectPerson(person);
    }
}
