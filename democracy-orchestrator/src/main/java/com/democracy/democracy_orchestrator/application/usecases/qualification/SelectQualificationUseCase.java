package com.democracy.democracy_orchestrator.application.usecases.qualification;

import com.democracy.democracy_orchestrator.domain.models.Qualification;
import com.democracy.democracy_orchestrator.domain.ports.in.qualification.SelectQualificationIn;
import com.democracy.democracy_orchestrator.domain.ports.out.QualificationOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SelectQualificationUseCase implements SelectQualificationIn {

    private final QualificationOut qualificationOut;

    public SelectQualificationUseCase(QualificationOut qualificationOut) {
        this.qualificationOut = qualificationOut;
    }

    @Override
    public Flux<Qualification> selectQualification(Qualification qualification) {
        return qualificationOut.selectQualification(qualification);
    }
}
