package ru.hits.attackdefenceplatform.core.competition.state;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.state.impl.CancelledCompetitionState;
import ru.hits.attackdefenceplatform.core.competition.state.impl.CompletedCompetitionState;
import ru.hits.attackdefenceplatform.core.competition.state.impl.InProgressCompetitionState;
import ru.hits.attackdefenceplatform.core.competition.state.impl.NewCompetitionState;
import ru.hits.attackdefenceplatform.core.competition.state.impl.PausedCompetitionState;

@Component
@RequiredArgsConstructor
public class CompetitionStateFactory {
    private final NewCompetitionState newState;
    private final InProgressCompetitionState inProgressState;
    private final PausedCompetitionState pausedState;
    private final CompletedCompetitionState completedState;
    private final CancelledCompetitionState cancelledState;

    public CompetitionState getState(CompetitionStatus status) {
        return switch (status) {
            case NEW -> newState;
            case IN_PROGRESS -> inProgressState;
            case PAUSED -> pausedState;
            case COMPLETED -> completedState;
            case CANCELLED -> cancelledState;
        };
    }

    public String getMessage(CompetitionAction action) {
        return switch (action) {
            case START -> "Соревнование началось!";
            case PAUSE -> "Соревнование приостановлено!";
            case RESUME -> "Соревнование возобновлено!";
            case COMPLETE -> "Соревнование завершено!";
            case CANCEL -> "Соревнование отменено!";
        };
    }
}
