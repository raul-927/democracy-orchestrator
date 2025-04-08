package com.democracy.validatedepartment.infrastructure.client;


import com.democracy.validatedepartment.domain.models.Department;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Component
@ReactiveFeignClient(name = "HumanResources")
public interface HttpBinClient {

    @GetMapping(path = "/delay/{delayInSeconds}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<Map<String, Object>> getBinWithDelayInSeconds(@PathVariable int delayInSeconds);
}
