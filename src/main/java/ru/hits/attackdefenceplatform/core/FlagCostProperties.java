package ru.hits.attackdefenceplatform.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "competition.settings")
@Getter
@Setter
public class FlagCostProperties {
    private int flagCost;
    private int flagLost;
}

