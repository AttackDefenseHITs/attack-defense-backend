package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.core.attack_bot.AttackBotConfigurationService;
import ru.hits.attackdefenceplatform.core.repo.RepositoryService;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionSettingsDto;

@Service
@RequiredArgsConstructor
public class CompetitionSettingsFacade {

    private final CompetitionContext competitionContext;
    private final AttackBotConfigurationService attackBotConfigurationService;
    private final RepositoryService repositoryService;

    public CompetitionSettingsDto getCurrentSettings() {
        var competition = competitionContext.getCurrent();
        var attackBotSettings = attackBotConfigurationService.getSettings();
        var repo = repositoryService.getCurrentRepositoryFromDB();

        return new CompetitionSettingsDto(
                competition.getName(),
                competition.getStatus(),
                competition.getStartDate(),
                competition.getEndDate(),
                competition.getTotalRounds(),
                competition.getRoundDurationMinutes(),
                competition.getCurrentRound(),
                competition.getFlagSendCost(),
                competition.getFlagLostCost(),
                competition.getRules(),
                competition.getCompetitionMode().name(),
                attackBotSettings,
                repo.htmlUrl()
        );
    }
}
