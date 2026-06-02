package ru.hits.attackdefenceplatform.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "competition.defaults")
@Data
public class CompetitionDefaultsProperties {
    private Integer totalRounds;
    private Integer roundDurationMinutes;
    private Integer flagSendCost;
    private Integer flagLostCost;
}
