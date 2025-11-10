package ru.hits.attackdefenceplatform.cron.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.round.CompetitionRoundService;
import ru.hits.attackdefenceplatform.modules.checker.CheckerExecutionService;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionStartNextRoundJob implements Job {

    private final CompetitionRoundService roundService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            roundService.tryAdvanceRound();
        } catch (Exception e) {
            log.error("Ошибка при выполнении джобы смены раунда", e);
            throw new JobExecutionException(e);
        }
    }
}
