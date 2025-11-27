package ru.hits.attackdefenceplatform.business.competition.round;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.DomainEventPublisher;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.business.CompetitionContext;
import ru.hits.attackdefenceplatform.business.competition.CompetitionService;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
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

    /**
     * Проверяет, можно ли перейти к следующему раунду, и делает это.
     */
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

        log.info("Текущий раунд завершен. Запускаем следующий...");
        var updatedCompetition = competitionService.startNextRound();

        // Публикуем событие о начале нового раунда
        eventPublisher.publish(new RoundStartedEvent(updatedCompetition.currentRound()));
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
}
