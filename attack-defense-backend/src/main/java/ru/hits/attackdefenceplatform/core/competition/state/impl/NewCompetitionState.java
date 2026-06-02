package ru.hits.attackdefenceplatform.core.competition.state.impl;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.state.AbstractCompetitionState;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class NewCompetitionState extends AbstractCompetitionState {
    @Override
    public void handle(Competition competition, CompetitionAction action) {
        switch (action) {
            case START -> {
                competition.setStatus(CompetitionStatus.IN_PROGRESS);
                competition.setStartDate(LocalDateTime.now(ZoneOffset.UTC));
                competition.setCurrentRound(0);
            }
            default -> unsupported(action, CompetitionStatus.NEW);
        }
    }

    @Override
    public List<CompetitionAction> getAvailableActions() {
        return List.of(CompetitionAction.START);
    }
}
