package ru.hits.attackdefenceplatform.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "competition.scoring")
@Data
public class CompetitionDynamicScoringProperties {
    private Double alpha;
    private Double gamma;
}
