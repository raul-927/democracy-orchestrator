package com.democracy.democracy_orchestrator.application.usecases.person;

import com.democracy.democracy_orchestrator.domain.models.Person;
import com.democracy.democracy_orchestrator.domain.ports.in.person.UpdatePersonIn;
import com.democracy.democracy_orchestrator.domain.ports.out.PersonOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UpdatePersonUseCase implements UpdatePersonIn {
    private final PersonOut personOut;

    public UpdatePersonUseCase(PersonOut personOut) {
        this.personOut = personOut;
    }

    @Override
    public Mono<Integer> updatePerson(Person person) {
        return this.personOut.updatePerson(person);
    }
}
