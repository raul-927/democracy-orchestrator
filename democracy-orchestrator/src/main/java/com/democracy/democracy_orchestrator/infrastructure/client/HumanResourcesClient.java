package com.democracy.democracy_orchestrator.infrastructure.client;


import com.democracy.democracy_orchestrator.domain.models.Department;
import com.democracy.democracy_orchestrator.domain.models.Investigation;
import com.democracy.democracy_orchestrator.infrastructure.web.constants.RoutesConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@FeignClient("HumanResources")
public interface HumanResourcesClient {
    static final String  HUMAN_RESOURCES = RoutesConstant.HUMAN_RESOURCES;
    static final String  DEPARTMENT      =  RoutesConstant.DEPARTMENT;
    static final String SELECT_ALL       = RoutesConstant.SELECT_ALL;
    static final String INVESTIGATION    = RoutesConstant.INVESTIGATION;

    @RequestMapping(value = HUMAN_RESOURCES + DEPARTMENT +SELECT_ALL)
    List<Department> getAllDepartment(@RequestHeader(name="Authorization") String token);

    @RequestMapping(value = HUMAN_RESOURCES + INVESTIGATION)
    Investigation selectInvestigation(@RequestBody Investigation investigation);

}
