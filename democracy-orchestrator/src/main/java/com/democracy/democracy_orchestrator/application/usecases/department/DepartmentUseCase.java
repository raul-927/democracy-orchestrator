package com.democracy.democracy_orchestrator.application.usecases.department;

import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.in.departments.SelectDepartmentIn;
import com.democracy.democracy_orchestrator.domain.ports.out.DepartmentOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DepartmentUseCase implements SelectDepartmentIn {

    private final DepartmentOut departmentsOut;

    public DepartmentUseCase(DepartmentOut departmentsOut) {
        this.departmentsOut = departmentsOut;
    }

    @Override
    public Flux<Department> selectDepartment(Department department) {
        return this.departmentsOut.selectDepartment(department);
    }
}
