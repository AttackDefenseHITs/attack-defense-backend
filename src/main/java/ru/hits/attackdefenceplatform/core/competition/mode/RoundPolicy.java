package ru.hits.attackdefenceplatform.core.competition.mode;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

public interface RoundPolicy {
    void onRoundStarted(Competition competition, int roundNumber);
}
