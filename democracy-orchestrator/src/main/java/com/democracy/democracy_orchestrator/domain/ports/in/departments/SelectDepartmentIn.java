package com.democracy.democracy_orchestrator.domain.ports.in.departments;

import com.democracy.democracy_orchestrator.domain.models.Department;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SelectDepartmentIn {

    List<Department> selectAllDepartment();
}
