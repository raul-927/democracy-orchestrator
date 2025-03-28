package demo.client;


import demo.domain.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("HumanResources")
public interface DepartmentFeingClient {

    //@PostMapping("/new")
    @RequestMapping(value = "/select-all", method = GET)
    Mono<Department> selectAllDepartment();
}
