package com.democracy.validatedepartment.application.usercases.humanresources.department;

import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.ports.in.humanresources.department.SelectDepartmentIn;
import com.democracy.validatedepartment.domain.ports.out.humanresources.DepartmentOut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class SelectDepartmentUseCase implements SelectDepartmentIn {
    private final DepartmentOut departmentOut;

    public SelectDepartmentUseCase(DepartmentOut departmentOut) {
        this.departmentOut = departmentOut;
    }

    @Override
    public Flux<Department> selectAllDepartment() {
        return this.departmentOut.selectAllDepartment();
    }
}
