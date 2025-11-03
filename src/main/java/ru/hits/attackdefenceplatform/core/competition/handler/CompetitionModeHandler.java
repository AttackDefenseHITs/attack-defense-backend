package ru.hits.attackdefenceplatform.core.competition.handler;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

public interface CompetitionModeHandler {
    void onStart(Competition competition);
    void onNextRound(Competition competition);
    void onComplete(Competition competition);
}