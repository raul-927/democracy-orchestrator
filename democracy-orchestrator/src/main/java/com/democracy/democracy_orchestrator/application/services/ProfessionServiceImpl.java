package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.domain.ports.in.profession.SelectProfessionIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProfessionServiceImpl implements ProfessionService{
    private final SelectProfessionIn selectProfessionIn;

    public ProfessionServiceImpl(SelectProfessionIn selectProfessionIn) {
        this.selectProfessionIn = selectProfessionIn;
    }

    @Override
    public Flux<Profession> selectProfession(Profession profession) {
        return this.selectProfessionIn.selectProfession(profession);
    }
}
