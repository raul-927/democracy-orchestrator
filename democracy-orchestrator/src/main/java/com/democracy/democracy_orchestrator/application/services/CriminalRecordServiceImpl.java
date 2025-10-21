package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.CriminalRecord;
import com.democracy.democracy_orchestrator.domain.ports.in.criminalrecord.SelectCriminalRecordIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CriminalRecordServiceImpl implements CriminalRecordService{

    private final SelectCriminalRecordIn criminalRecordIn;

    public CriminalRecordServiceImpl(SelectCriminalRecordIn criminalRecordIn) {
        this.criminalRecordIn = criminalRecordIn;
    }

    @Override
    public Flux<CriminalRecord> selectCriminalRecord(CriminalRecord criminalRecord) {
        return criminalRecordIn.selectCriminalRecord(criminalRecord);
    }
}
