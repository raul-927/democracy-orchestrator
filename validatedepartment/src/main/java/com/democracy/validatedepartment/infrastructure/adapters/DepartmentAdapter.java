package com.democracy.validatedepartment.infrastructure.adapters;

import com.democracy.validatedepartment.application.services.TokenService;
import com.democracy.validatedepartment.domain.models.Department;
import com.democracy.validatedepartment.domain.ports.out.humanresources.DepartmentOut;
import com.democracy.validatedepartment.infrastructure.client.DepartmentFeingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentAdapter implements DepartmentOut {

    @Autowired
    private DepartmentFeingClient departmentFeingClient;
    @Autowired
    private TokenService tokenService;

    @Override
    public List<Department> selectAllDepartment() {
        return departmentFeingClient.getAllDepartment(tokenService.obtainToken());
    }
}
