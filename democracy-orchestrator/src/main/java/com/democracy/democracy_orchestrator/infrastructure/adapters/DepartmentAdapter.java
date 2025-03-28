package com.democracy.democracy_orchestrator.infrastructure.adapters;


import com.democracy.democracy_orchestrator.application.services.TokenService;
import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.ports.out.DepartmentsOut;
import com.democracy.democracy_orchestrator.infrastructure.client.HumanResourcesClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class DepartmentAdapter implements DepartmentsOut {

    @Autowired
    private HumanResourcesClient humanResourcesClient;
    @Autowired
    private TokenService tokenService;

    @Override
    public List<Department> selectAllDepartment() {
        return humanResourcesClient.getAllDepartment(tokenService.obtainToken());
    }
}
