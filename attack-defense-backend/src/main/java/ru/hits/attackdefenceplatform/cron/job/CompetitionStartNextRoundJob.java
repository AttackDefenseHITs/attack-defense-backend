package ru.hits.attackdefenceplatform.cron.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.round.CompetitionRoundService;

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
