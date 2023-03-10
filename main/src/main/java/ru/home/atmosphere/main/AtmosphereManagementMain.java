package ru.home.atmosphere.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AtmosphereManagementMain {

    public static void main(String[] args) {
        SpringApplication.run(AtmosphereManagementMain.class, args);
    }

}
