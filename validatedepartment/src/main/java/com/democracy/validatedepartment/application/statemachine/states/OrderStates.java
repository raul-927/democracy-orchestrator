package com.democracy.validatedepartment.application.statemachine.states;

public enum OrderStates {
    NEW, VALIDATED, PAID, SHIPPED, COMPLETED, CANCELLED, STATE_MACHINE_STOPED;
}
