package ru.hits.attackdefenceplatform.core.competition.mode;


import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.util.UUID;

public interface FlagSubmissionPolicy {

    /**
     * Можно ли в принципе сдавать флаги в этом режиме (по состоянию соревнования и т.п.)
     */
    void validateSubmissionAllowed(Competition competition);

    /**
     * Сколько очков дать за успешную сдачу флага.
     */
    double calculatePoints(Competition competition,
                           UUID teamId,
                           UUID serviceId,
                           int currentRound);
}
