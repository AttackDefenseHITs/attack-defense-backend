package ru.hits.attackdefenceplatform.core.points.sla.metric;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamServiceSlaRoundSnapshot {
    private UUID teamId;
    private UUID serviceId;
    private long roundNumber;
    private long totalDuration;
    private long totalOkDuration;
    private LocalDateTime capturedAt;
}
