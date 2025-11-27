package ru.hits.attackdefenceplatform.business.competition.state;

import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;

import java.util.List;

public interface CompetitionState {
    void handle(Competition competition, CompetitionAction action);
    List<CompetitionAction> getAvailableActions();
}

