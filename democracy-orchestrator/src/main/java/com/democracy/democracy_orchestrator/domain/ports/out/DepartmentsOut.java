package com.democracy.democracy_orchestrator.domain.ports.out;

import com.democracy.democracy_orchestrator.domain.models.Department;
import reactor.core.publisher.Flux;

import java.util.List;

public interface DepartmentsOut {

    List<Department> selectAllDepartment();
}
