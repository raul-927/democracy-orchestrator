package com.democracy.democracy_orchestrator.application.usecases.person;

import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.person.SelectPersonIn;
import com.democracy.democracy_orchestrator.domain.ports.out.PersonOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SelectPersonUseCase implements SelectPersonIn {
    private final PersonOut personOut;

    public SelectPersonUseCase(PersonOut personOut) {
        this.personOut = personOut;
    }

    @Override
    public Flux<Person> selectPerson(Person person) {
        return this.personOut.selectPerson(person);
    }
}
