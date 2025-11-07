package ru.hits.attackdefenceplatform.core.competition.state;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.util.List;

public interface CompetitionState {
    void handle(Competition competition, CompetitionAction action);
    List<CompetitionAction> getAvailableActions();
}

