package com.mehedi.spring_security_client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public String sayHello(Principal principal) {
        return "Hello World";
    }
}
