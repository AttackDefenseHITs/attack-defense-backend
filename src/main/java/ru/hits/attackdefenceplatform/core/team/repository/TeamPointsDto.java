package ru.hits.attackdefenceplatform.core.team.repository;


import java.util.UUID;

public class TeamPointsDto {
    private UUID teamId;
    private Long points;

    public TeamPointsDto(UUID teamId, Long points) {
        this.teamId = teamId;
        this.points = points;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "TeamPointsDto{" +
                "teamId=" + teamId +
                ", points=" + points +
                '}';
    }
}
