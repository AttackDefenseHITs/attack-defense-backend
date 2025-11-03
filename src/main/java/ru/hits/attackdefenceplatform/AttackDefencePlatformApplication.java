package ru.hits.attackdefenceplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AttackDefencePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttackDefencePlatformApplication.class, args);
    }

}
