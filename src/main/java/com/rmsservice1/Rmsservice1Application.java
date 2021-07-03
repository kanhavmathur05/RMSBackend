package com.rmsservice1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Rmsservice1Application {

    public static void main(String[] args) {
        SpringApplication.run(Rmsservice1Application.class, args);
    }

}
