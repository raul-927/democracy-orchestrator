package com.democracy.validatedepartment.application.services;

import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.ports.in.humanresources.department.SelectDepartmentIn;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{
    private final SelectDepartmentIn selectDepartmentIn;

    public DepartmentServiceImpl(SelectDepartmentIn selectDepartmentIn) {
        this.selectDepartmentIn = selectDepartmentIn;
    }

    @Override
    public Flux<Department> selectAllDepartment() {
        return this.selectDepartmentIn.selectAllDepartment();
    }
}
