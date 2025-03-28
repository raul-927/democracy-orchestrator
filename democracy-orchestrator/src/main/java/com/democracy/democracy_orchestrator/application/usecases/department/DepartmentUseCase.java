package com.democracy.democracy_orchestrator.application.usecases.department;

import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.in.departments.SelectDepartmentIn;
import com.democracy.democracy_orchestrator.domain.ports.out.DepartmentsOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class DepartmentUseCase implements SelectDepartmentIn {

    private final DepartmentsOut departmentsOut;

    public DepartmentUseCase(DepartmentsOut departmentsOut) {
        this.departmentsOut = departmentsOut;
    }

    @Override
    public List<Department> selectAllDepartment() {
        return this.departmentsOut.selectAllDepartment();
    }
}
