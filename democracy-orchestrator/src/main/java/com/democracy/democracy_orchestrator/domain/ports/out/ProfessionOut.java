package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Profession;
import reactor.core.publisher.Flux;

public interface ProfessionOut {

    Flux<Profession> selectProfession(Profession profession);
}
