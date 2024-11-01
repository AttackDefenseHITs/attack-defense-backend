package ru.hits.attackdefenceplatform.public_interface.team;

public record CreateTeamRequest(
        String name,
        Integer maxMembers
) {
}
