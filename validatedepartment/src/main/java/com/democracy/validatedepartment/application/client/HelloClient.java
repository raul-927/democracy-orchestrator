package com.democracy.validatedepartment.application.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient("HelloServer")
public interface HelloClient {

    @RequestMapping(value = "/", method = GET)
    String hello();
}
