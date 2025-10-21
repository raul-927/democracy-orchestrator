package com.democracy.democracy_orchestrator.application.usecases.criminalrecord;

import com.democracy.democracy_orchestrator.domain.models.CriminalRecord;
import com.democracy.democracy_orchestrator.domain.ports.in.criminalrecord.SelectCriminalRecordIn;
import com.democracy.democracy_orchestrator.domain.ports.out.CriminalRecordOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class CriminalRecordUseCase implements SelectCriminalRecordIn {

    private final CriminalRecordOut criminalRecordOut;

    public CriminalRecordUseCase(CriminalRecordOut criminalRecordOut) {
        this.criminalRecordOut = criminalRecordOut;
    }

    @Override
    public Flux<CriminalRecord> selectCriminalRecord(CriminalRecord criminalRecord) {
        return criminalRecordOut.selectCriminalRecord(criminalRecord);
    }
}
