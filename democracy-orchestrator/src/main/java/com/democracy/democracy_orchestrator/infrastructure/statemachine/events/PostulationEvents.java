package com.democracy.democracy_orchestrator.infrastructure.statemachine.events;

public enum PostulationEvents {
    VALIDATE_PERSON, SEND_RESULT_VALIDATED_PERSON,
    VALIDATE_PROFESSION,
    SEND_RESULT_CRIMINAL_RECORD_VALIDATED,
    SEND_RESULT_VALIDATE_DOCUMENT,
    SEND_RESULT_VALIDATE_QUALIFICATIONS,
    SEND_ERROR;
}
