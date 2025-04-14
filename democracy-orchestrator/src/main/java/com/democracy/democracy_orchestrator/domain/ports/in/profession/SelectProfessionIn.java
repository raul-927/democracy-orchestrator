package com.democracy.democracy_orchestrator.domain.ports.in.profession;

import com.democracy.democracy_orchestrator.domain.models.Profession;
import reactor.core.publisher.Flux;

public interface SelectProfessionIn {
    Flux<Profession> selectProfession(Profession profession);
}
