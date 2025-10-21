package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Qualification;
import reactor.core.publisher.Flux;

public interface QualificationOut {
    Flux<Qualification> selectQualification(Qualification qualification);
}
