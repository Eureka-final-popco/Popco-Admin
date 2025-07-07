package com.popcoadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PopcoAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopcoAdminApplication.class, args);
    }

}
