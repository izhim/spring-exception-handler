package com.jose.springboot.error.springboot_error.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class AppController {

    @GetMapping("/app")
    public String index() {
        int i = 1 / 0;
        System.out.println(i);
        return "Hello World";
    }
}
