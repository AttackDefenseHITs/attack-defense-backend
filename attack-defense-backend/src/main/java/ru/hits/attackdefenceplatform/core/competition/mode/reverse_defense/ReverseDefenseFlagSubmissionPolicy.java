package ru.hits.attackdefenceplatform.core.competition.mode.reverse_defense;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.mode.FlagSubmissionPolicy;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.util.UUID;

@Component
public class ReverseDefenseFlagSubmissionPolicy implements FlagSubmissionPolicy {

    @Override
    public void validateSubmissionAllowed(Competition competition) {
        throw new CompetitionException("В режиме Reverse-Defense сдача флагов недоступна");
    }

    @Override
    public double calculatePoints(Competition competition, UUID teamId,
                                  UUID serviceId, int currentRound) {
        return 0.0;
    }
}

