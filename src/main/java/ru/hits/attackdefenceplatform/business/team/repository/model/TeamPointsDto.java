package ru.hits.attackdefenceplatform.business.team.repository.model;

import java.util.UUID;

public record TeamPointsDto(UUID teamId, double points) {}