package ru.hits.attackdefenceplatform.core.competition.job;

import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CompetitionCompletionJob implements Job {
    private final CompetitionService competitionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var competition = competitionService.getCompetition();

        if (competition.getStatus() == CompetitionStatus.IN_PROGRESS &&
                LocalDateTime.now().isAfter(competition.getEndDate())) {
            competitionService.changeCompetitionStatus(CompetitionAction.COMPLETE);
        }
    }
}
