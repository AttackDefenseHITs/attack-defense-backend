package ru.hits.attackdefenceplatform.core.competition.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionChangeStatusJob implements Job {
    private final CompetitionService competitionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        var competition = competitionService.getCompetition();

        var startDateUTC = competition.getStartDate() != null ? competition.getStartDate().atZone(ZoneOffset.UTC) : null;
        var endDateUTC = competition.getEndDate() != null ? competition.getEndDate().atZone(ZoneOffset.UTC) : null;
        var nowUTC = ZonedDateTime.now(ZoneOffset.UTC);

        if (competition.getStatus() == CompetitionStatus.NEW
                && startDateUTC != null && isDateValid(startDateUTC, nowUTC::isAfter)) {
            competitionService.changeCompetitionStatus(CompetitionAction.START);
        }
        else if (competition.getStatus().equals(CompetitionStatus.IN_PROGRESS)
                && endDateUTC != null && isDateValid(endDateUTC, nowUTC::isAfter)) {
            competitionService.changeCompetitionStatus(CompetitionAction.COMPLETE);
        }
    }

    private boolean isDateValid(ZonedDateTime date, Function<ZonedDateTime, Boolean> validator) {
        return Optional.ofNullable(date)
                .map(validator)
                .orElse(false);
    }
}





