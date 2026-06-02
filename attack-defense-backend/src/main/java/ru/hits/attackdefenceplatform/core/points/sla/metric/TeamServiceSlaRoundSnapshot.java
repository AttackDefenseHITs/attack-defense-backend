package ru.hits.attackdefenceplatform.core.points.sla.metric;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamServiceSlaRoundSnapshot {
    private UUID teamId;
    private UUID serviceId;
    private long roundNumber;
    private CheckerResult status;
    private LocalDateTime capturedAt;
}
