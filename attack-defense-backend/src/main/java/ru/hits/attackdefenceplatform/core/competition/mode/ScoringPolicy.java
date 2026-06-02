package ru.hits.attackdefenceplatform.core.competition.mode;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.util.UUID;

public interface ScoringPolicy {
    double calculateTeamScore(Competition competition, UUID teamId);
}
