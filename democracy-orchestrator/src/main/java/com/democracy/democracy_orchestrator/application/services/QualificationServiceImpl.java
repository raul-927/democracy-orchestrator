package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Qualification;
import com.democracy.democracy_orchestrator.domain.ports.in.qualification.SelectQualificationIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class QualificationServiceImpl implements QualificationService{

    private final SelectQualificationIn selectQualificationIn;

    public QualificationServiceImpl(SelectQualificationIn selectQualificationIn) {
        this.selectQualificationIn = selectQualificationIn;
    }

    @Override
    public Flux<Qualification> selectQualification(Qualification qualification) {
        return selectQualificationIn.selectQualification(qualification);
    }
}
