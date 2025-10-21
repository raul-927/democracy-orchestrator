package com.democracy.democracy_orchestrator.domain.ports.in.qualification;

import com.democracy.democracy_orchestrator.domain.models.Qualification;
import reactor.core.publisher.Flux;

public interface SelectQualificationIn {

    Flux<Qualification> selectQualification(Qualification qualification);
}
