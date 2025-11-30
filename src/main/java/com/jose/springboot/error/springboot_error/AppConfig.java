package com.jose.springboot.error.springboot_error;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jose.springboot.error.springboot_error.models.domain.User;

@Configuration
public class AppConfig {

    @Bean
    List<User> users(){
        return Arrays.asList(
            new User(1L, "Pepe", "Gonzalez"),
            new User(2L, "Maria", "Perez"),
            new User(3L, "Juan", "Castro"),
            new User(4L, "Manuel", "Chinchilla")
        );
    }

}
