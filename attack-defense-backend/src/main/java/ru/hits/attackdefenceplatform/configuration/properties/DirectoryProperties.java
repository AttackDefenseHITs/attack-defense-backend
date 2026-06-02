package ru.hits.attackdefenceplatform.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "directory")
@Configuration
@Data
public class DirectoryProperties {
    private String checkers;
    private String exploits;
}
