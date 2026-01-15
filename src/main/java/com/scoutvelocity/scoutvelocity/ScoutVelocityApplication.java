package com.scoutvelocity.scoutvelocity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ScoutVelocityApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScoutVelocityApplication.class, args);
    }

}
