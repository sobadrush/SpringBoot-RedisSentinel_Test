package com.nanshan.springbootredissentinel_test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RogerLo
 * @date 2023/10/27
 */
@RestController
@RequestMapping("/HelloController")
public class HelloController {

    // http://localhost:8080/HelloController/sayHelloRedis
    @GetMapping("/sayHelloRedis")
    public String sayHelloRedis() {
        return "Hello Redis";
    }

}
