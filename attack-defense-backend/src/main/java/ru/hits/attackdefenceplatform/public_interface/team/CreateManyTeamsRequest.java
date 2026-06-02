package ru.hits.attackdefenceplatform.public_interface.team;

public record CreateManyTeamsRequest(
        Long teamsCount,
        Long maxMembers
) { }
