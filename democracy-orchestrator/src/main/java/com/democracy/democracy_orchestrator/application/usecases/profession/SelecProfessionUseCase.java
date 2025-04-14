package com.democracy.democracy_orchestrator.application.usecases.profession;

import com.democracy.democracy_orchestrator.domain.models.Profession;
import com.democracy.democracy_orchestrator.domain.ports.in.profession.SelectProfessionIn;
import com.democracy.democracy_orchestrator.domain.ports.out.ProfessionOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SelecProfessionUseCase implements SelectProfessionIn {
    private final ProfessionOut professionOut;

    public SelecProfessionUseCase(ProfessionOut professionOut) {
        this.professionOut = professionOut;
    }

    @Override
    public Flux<Profession> selectProfession(Profession profession) {
        return this.professionOut.selectProfession(profession);
    }
}
