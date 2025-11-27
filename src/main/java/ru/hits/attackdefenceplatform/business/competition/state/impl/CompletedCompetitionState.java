package ru.hits.attackdefenceplatform.business.competition.state.impl;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.state.AbstractCompetitionState;

import java.util.List;

@Component
public class CompletedCompetitionState extends AbstractCompetitionState {
    @Override
    public void handle(Competition competition, CompetitionAction action) {
        unsupported(action, CompetitionStatus.COMPLETED);
    }

    @Override
    public List<CompetitionAction> getAvailableActions() {
        return List.of(CompetitionAction.START);
    }
}