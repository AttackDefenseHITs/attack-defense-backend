package ru.hits.attackdefenceplatform.core.competition.round;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.DomainEventPublisher;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.points.sla.TeamRoundCheckSnapshotService;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.publisher.RoundStartedEvent;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionRoundService {

    private final CompetitionService competitionService;
    private final CompetitionContext competitionContext;
    private final DomainEventPublisher eventPublisher;
    private final TeamRoundCheckSnapshotService snapshotService;

    @Transactional
    public void tryAdvanceRound() {
        var competition = competitionContext.getCurrent();

        if (!competitionContext.isInProgress()) {
            log.debug("Соревнование не активно, пропускаем обновление раунда");
            return;
        }

        if (!isCurrentRoundFinished(competition)) {
            log.debug("Текущий раунд еще не завершен");
            return;
        }

        long finishedRound = competition.getCurrentRound();
        snapshotService.captureFinishedRound(finishedRound);

        log.info("Текущий раунд завершен. Запускаем следующий...");
        var updatedCompetition = competitionService.startNextRound();

        var newRoundStartTime = calculateRoundStartTime(updatedCompetition);

        eventPublisher.publish(new RoundStartedEvent(
                updatedCompetition.currentRound(),
                newRoundStartTime
        ));
    }

    private boolean isCurrentRoundFinished(Competition competition) {
        var startDate = competition.getStartDate();
        if (startDate == null) {
            throw new CompetitionException("Дата начала соревнования не задана");
        }

        int roundDuration = competition.getRoundDurationMinutes();
        int currentRound = competition.getCurrentRound();

        var roundEndTime = startDate.plusMinutes((long) (currentRound + 1) * roundDuration);
        return LocalDateTime.now(ZoneOffset.UTC).isAfter(roundEndTime);
    }

    private LocalDateTime calculateRoundStartTime(CompetitionDto competition) {
        var startDate = competition.startDate();
        if (startDate == null) {
            throw new CompetitionException("Дата начала соревнования не задана");
        }

        int roundDuration = competition.roundDurationMinutes();
        int currentRound = competition.currentRound();

        return startDate.plusMinutes((long) currentRound * roundDuration);
    }
}
