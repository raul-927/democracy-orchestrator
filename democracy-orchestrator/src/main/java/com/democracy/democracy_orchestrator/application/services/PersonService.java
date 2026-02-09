package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.ports.in.person.SelectPersonIn;
import com.democracy.democracy_orchestrator.domain.ports.in.person.UpdatePersonIn;

public interface PersonService extends SelectPersonIn, UpdatePersonIn {
}
