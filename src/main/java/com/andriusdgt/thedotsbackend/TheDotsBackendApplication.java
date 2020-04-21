package com.andriusdgt.thedotsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:context/context.xml")
public class TheDotsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheDotsBackendApplication.class, args);
    }

}
