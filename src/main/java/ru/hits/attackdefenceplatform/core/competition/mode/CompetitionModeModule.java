package ru.hits.attackdefenceplatform.core.competition.mode;

import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.util.UUID;

public interface CompetitionModeModule {

    CompetitionMode mode();

    default void onStart(Competition competition) {}
    default void onNextRound(Competition competition, int newRound) {}
    default void onComplete(Competition competition) {}
    default void onRestart(Competition competition) {}

    default FlagSubmissionPolicy flagSubmissionPolicy() {
        return new FlagSubmissionPolicy() {
            @Override
            public void validateSubmissionAllowed(Competition competition) {
                // по умолчанию: нельзя сдавать флаги
                throw new CompetitionException("Флаги в этом режиме не поддерживаются");
            }

            @Override
            public double calculatePoints(Competition competition, UUID teamId, UUID serviceId, int currentRound) {
                return 0.0;
            }
        };
    }

    default ScoringPolicy scoringPolicy() {
        return (competition, teamId) -> 0.0;
    }

    default RoundPolicy roundPolicy() {
        return (competition, round) -> { };
    }
}
