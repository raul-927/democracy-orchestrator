package com.democracy.democracy_orchestrator.application.services;

import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.in.departments.SelectDepartmentIn;
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
    public List<Department> selectAllDepartment() {
        return this.selectDepartmentIn.selectAllDepartment();
    }
}
