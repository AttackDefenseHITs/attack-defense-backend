package ru.hits.attackdefenceplatform.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "checkers")
@Configuration
@Data
public class CheckersProperties {
    private String directory;
}
