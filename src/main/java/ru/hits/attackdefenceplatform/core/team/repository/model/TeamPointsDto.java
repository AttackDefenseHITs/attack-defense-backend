package ru.hits.attackdefenceplatform.core.team.repository.model;

import java.util.UUID;

public record TeamPointsDto(UUID teamId, double points) {}