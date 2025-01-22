package ru.hits.attackdefenceplatform.core.competition.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionStartNextRoundJob implements Job {
    private final CompetitionService competitionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            var competition = competitionService.getCompetition();
            if (!isCompetitionInProgress(competition)) {
                log.info("Соревнование не в статусе IN_PROGRESS, раунды не меняются.");
                return;
            }

            if (isCurrentRoundFinished(competition)) {
                competitionService.startNextRound();
            }

        } catch (CompetitionException e) {
            log.warn("Не удалось сменить раунд: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Произошла ошибка при смене раунда:", e);
            throw new JobExecutionException("Ошибка при смене раунда", e);
        }
    }

    /**
     * Проверяет, находится ли соревнование в статусе IN_PROGRESS.
     *
     * @param competition текущий объект соревнования
     * @return true, если статус IN_PROGRESS; false в противном случае
     */
    private boolean isCompetitionInProgress(Competition competition) {
        return competition.getStatus() == CompetitionStatus.IN_PROGRESS;
    }

    /**
     * Проверяет, завершился ли текущий раунд по времени.
     *
     * @param competition текущий объект соревнования
     * @return true, если текущий раунд завершён; false в противном случае
     */
    private boolean isCurrentRoundFinished(Competition competition) {
        var startDate = competition.getStartDate();
        int roundDurationMinutes = competition.getRoundDurationMinutes();
        int currentRound = competition.getCurrentRound();

        var currentRoundEndTime = startDate.plusMinutes((long) roundDurationMinutes * currentRound + (long) roundDurationMinutes);

        return LocalDateTime.now(ZoneOffset.UTC).isAfter(currentRoundEndTime);
    }
}
