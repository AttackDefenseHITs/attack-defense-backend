package ru.hits.attackdefenceplatform.business.competition.state.impl;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.state.AbstractCompetitionState;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class InProgressCompetitionState extends AbstractCompetitionState {
    @Override
    public void handle(Competition competition, CompetitionAction action) {
        switch (action) {
            case COMPLETE -> {
                competition.setStatus(CompetitionStatus.COMPLETED);
                competition.setEndDate(LocalDateTime.now(ZoneOffset.UTC));
            }
            case PAUSE -> competition.setStatus(CompetitionStatus.PAUSED);
            case CANCEL -> competition.setStatus(CompetitionStatus.CANCELLED);
            default -> unsupported(action, CompetitionStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<CompetitionAction> getAvailableActions() {
        return List.of(CompetitionAction.COMPLETE, CompetitionAction.PAUSE, CompetitionAction.CANCEL);
    }
}
