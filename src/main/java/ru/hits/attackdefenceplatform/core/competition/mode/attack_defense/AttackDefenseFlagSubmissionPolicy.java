package ru.hits.attackdefenceplatform.core.competition.mode.attack_defense;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.mode.FlagSubmissionPolicy;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.points.DynamicPointsCalculator;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttackDefenseFlagSubmissionPolicy implements FlagSubmissionPolicy {

    private final DynamicPointsCalculator dynamicPointsCalculator;

    @Override
    public void validateSubmissionAllowed(Competition competition) {
        if (competition.getCompetitionMode() != CompetitionMode.ATTACK_DEFENSE) {
            throw new CompetitionException("Флаг можно сдавать только в режиме Attack-Defense");
        }
        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS ||
                competition.getCurrentRound() == 0) {
            throw new CompetitionException("Флаг сдавать в текущий момент нельзя");
        }
    }

    @Override
    public double calculatePoints(Competition competition,
                                  UUID teamId,
                                  UUID serviceId,
                                  int currentRound) {
        return dynamicPointsCalculator.calculate(teamId, serviceId, currentRound);
    }
}
