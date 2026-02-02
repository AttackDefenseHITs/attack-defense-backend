package ru.hits.attackdefenceplatform.public_interface.team_stats;

public record ServiceStatsDto(
        String name,
        Boolean online,
        Integer uptimePct
) {
}
