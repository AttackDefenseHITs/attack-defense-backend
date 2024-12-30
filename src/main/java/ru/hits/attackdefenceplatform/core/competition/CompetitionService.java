package ru.hits.attackdefenceplatform.core.competition;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.util.List;

public interface CompetitionService {
    CompetitionDto changeCompetitionStatus(CompetitionAction action);
    List<CompetitionAction> getAvailableActions();
    CompetitionDto updateCompetition(UpdateCompetitionRequest request);
    CompetitionDto getCompetitionDto();
    Competition getCompetition();
}
