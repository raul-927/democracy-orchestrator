package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Department;
import reactor.core.publisher.Flux;

public interface DepartmentOut {

    Flux<Department> selectDepartment(Department department);
}
