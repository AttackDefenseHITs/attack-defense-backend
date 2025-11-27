package ru.hits.attackdefenceplatform.business.competition.state;

import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionStatus;

public abstract class AbstractCompetitionState implements CompetitionState {

    protected void unsupported(CompetitionAction action, CompetitionStatus status) {
        throw new CompetitionException(
                String.format("Действие %s недопустимо в состоянии %s", action, status)
        );
    }
}
