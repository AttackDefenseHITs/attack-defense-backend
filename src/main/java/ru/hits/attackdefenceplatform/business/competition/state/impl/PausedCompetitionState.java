package ru.hits.attackdefenceplatform.business.competition.state.impl;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.state.AbstractCompetitionState;

import java.util.List;

@Component
public class PausedCompetitionState extends AbstractCompetitionState {
    @Override
    public void handle(Competition competition, CompetitionAction action) {
        switch (action) {
            case RESUME -> competition.setStatus(CompetitionStatus.IN_PROGRESS);
            case CANCEL -> competition.setStatus(CompetitionStatus.CANCELLED);
            default -> unsupported(action, CompetitionStatus.PAUSED);
        }
    }

    @Override
    public List<CompetitionAction> getAvailableActions() {
        return List.of(CompetitionAction.RESUME, CompetitionAction.CANCEL);
    }
}

