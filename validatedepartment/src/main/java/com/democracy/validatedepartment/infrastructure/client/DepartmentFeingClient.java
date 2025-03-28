package com.democracy.validatedepartment.infrastructure.client;



import com.democracy.validatedepartment.domain.models.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("HumanResources")
public interface DepartmentFeingClient {
    @RequestMapping(value = "/humanresources/prueba/text", method = GET, headers = "Authorization")

    String getPrueba(@RequestHeader(name="Authorization") String token);


    @RequestMapping(value = "/humanresources/department/select-all", method = GET, headers = "Authorization")
    List<Department> getAllDepartment(@RequestHeader(name="Authorization") String token);
}
