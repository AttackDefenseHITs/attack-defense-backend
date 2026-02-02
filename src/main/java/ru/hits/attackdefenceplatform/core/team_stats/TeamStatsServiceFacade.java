package ru.hits.attackdefenceplatform.core.team_stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseRepository;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team_stats.ServiceStatsDto;
import ru.hits.attackdefenceplatform.public_interface.team_stats.TeamStatsDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamStatsServiceFacade {

    private final TeamMemberRepository teamMemberRepository;
    private final ServiceHintPurchaseRepository serviceHintPurchaseRepository;
    private final ServiceStatusRepository serviceStatusRepository;

    public TeamStatsDto getTeamStats(UserEntity user) {
        if (user == null) return emptyDto();

        var teamMemberOpt = teamMemberRepository.findByUser(user);
        if (teamMemberOpt.isEmpty() || teamMemberOpt.get().getTeam() == null) {
            return emptyDto();
        }

        var team = teamMemberOpt.get().getTeam();
        UUID teamId = team.getId();

        int hintsBought = safeInt(serviceHintPurchaseRepository.countByTeam_Id(teamId));
        var statuses = serviceStatusRepository.findByTeam_Id(teamId);

        var byService = statuses.stream()
                .collect(Collectors.toMap(
                        s -> s.getService().getId(),
                        s -> s,
                        (a, b) -> a.getUpdatedAt().isAfter(b.getUpdatedAt()) ? a : b
                ));

        int totalCount = byService.size();

        int onlineCount = (int) byService.values().stream()
                .filter(s -> s.getLastStatus() == CheckerResult.OK)
                .count();

        boolean online = totalCount > 0 && onlineCount == totalCount;
        int avgUptime = computeAvgUptimePercent(byService.values());

        String stack = formatTodStack(byService.values(), onlineCount, totalCount);

        var services = byService.values().stream()
                .map(ss -> new ServiceStatsDto(
                        safeServiceName(ss),
                        ss.getLastStatus() == CheckerResult.OK,
                        computeUptimePercent(ss)
                ))
                .toList();

        return new TeamStatsDto(
                teamId != null ? teamId.toString() : "",
                team.getName() != null ? team.getName() : "",
                online,
                stack,
                onlineCount,
                totalCount,
                hintsBought,
                avgUptime,
                services
        );
    }

    private String formatTodStack(Collection<ServiceStatusEntity> statuses, int onlineCount, int totalCount) {
        //String stackPart = totalCount > 0 ? (onlineCount + "/" + totalCount) : "—";

        long todSeconds = computeTeamTodSeconds(statuses, onlineCount, totalCount);
        String todPart = formatDuration(todSeconds);

        return todPart;
    }

    private long computeTeamTodSeconds(Collection<ServiceStatusEntity> statuses, int onlineCount, int totalCount) {
        if (statuses == null || statuses.isEmpty()) return 0;
        if (totalCount <= 0) return 0;
        if (onlineCount != totalCount) return 0;

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        return statuses.stream()
                .filter(s -> s.getLastStatus() == CheckerResult.OK && s.getLastChanged() != null)
                .mapToLong(s -> Math.max(0, Duration.between(s.getLastChanged(), now).getSeconds()))
                .min()
                .orElse(0);
    }

    private String formatDuration(long seconds) {
        if (seconds <= 0) return "0m";
        long mins = seconds / 60;
        long hrs = mins / 60;
        long days = hrs / 24;

        if (days > 0) return days + "d " + (hrs % 24) + "h";
        if (hrs > 0) return hrs + "h " + (mins % 60) + "m";
        return mins + "m";
    }

    private TeamStatsDto emptyDto() {
        return new TeamStatsDto(
                "",
                "",
                false,
                "ToD: 0m / Stack: —",
                0,
                0,
                0,
                0,
                List.of()
        );
    }

    private int computeAvgUptimePercent(Collection<ServiceStatusEntity> statuses) {
        if (statuses == null || statuses.isEmpty()) return 0;

        double avg = statuses.stream()
                .mapToInt(this::computeUptimePercent)
                .average()
                .orElse(0.0);

        return (int) Math.round(avg);
    }

    private int computeUptimePercent(ServiceStatusEntity ss) {
        long total = ss.getTotalDuration();
        if (total <= 0) return 0;
        double pct = (ss.getTotalOkDuration() * 100.0) / total;
        return (int) Math.round(pct);
    }

    private String safeServiceName(ServiceStatusEntity ss) {
        return ss.getService() != null && ss.getService().getName() != null
                ? ss.getService().getName()
                : "unknown";
    }

    private int safeInt(long v) {
        return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
    }
}
