package com.app.onestepback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OneStepBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneStepBackApplication.class, args);
    }

}
