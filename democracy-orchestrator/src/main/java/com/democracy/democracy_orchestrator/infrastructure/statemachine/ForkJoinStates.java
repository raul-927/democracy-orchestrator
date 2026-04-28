package com.democracy.democracy_orchestrator.infrastructure.statemachine;

public enum ForkJoinStates {
    INITIAL,
    FORK_POINT,
    BRANCH_1, BRANCH_1_DONE,
    BRANCH_2, BRANCH_2_DONE,
    BRANCH_3, BRANCH_3_DONE,
    BRANCH_4, BRANCH_4_DONE,
    JOIN_POINT,
    FINAL
}
