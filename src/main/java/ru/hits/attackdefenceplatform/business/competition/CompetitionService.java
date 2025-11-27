package ru.hits.attackdefenceplatform.business.competition;

import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionModeRequest;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.util.List;

public interface CompetitionService {
    CompetitionDto changeCompetitionStatus(CompetitionAction action);
    List<CompetitionAction> getAvailableActions();
    CompetitionDto updateCompetition(UpdateCompetitionRequest request);
    CompetitionDto updateCompetitionMode(UpdateCompetitionModeRequest request);
    CompetitionDto getCompetitionDto();
    Competition getCompetition();
    CompetitionDto restartCompetition();
    CompetitionDto startNextRound();
}
