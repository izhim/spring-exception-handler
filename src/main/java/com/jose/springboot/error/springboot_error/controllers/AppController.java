package com.jose.springboot.error.springboot_error.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jose.springboot.error.springboot_error.exceptions.UserNotFoundException;
import com.jose.springboot.error.springboot_error.models.domain.User;
import com.jose.springboot.error.springboot_error.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private UserService service;

    @GetMapping("/index")
    public String index() {
        int i = 1 / 0;
        System.out.println(i);
        return "Hello World";
    }

    @GetMapping("/number")
    public String number(){
        int i = Integer.parseInt("1s");
        return "ok 200" + i;
    }

    @GetMapping("/show/{id}")
    public User show(@PathVariable(name = "id") Long id){
        User user = service.findById(id).orElseThrow(() -> new UserNotFoundException("Error: User does not exists"));
        //System.out.print(user.getName());
        return user;
    }
    
    
}
