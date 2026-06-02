package ru.hits.attackdefenceplatform.public_interface.team_stats;

import java.util.List;

public record TeamStatsDto(
        String id,
        String name,
        Boolean online,
        String stack,
        Integer onlineCount,
        Integer totalCount,
        Integer hintsBought,
        Integer avgUptime,
        List<ServiceStatsDto> services
) {
}
