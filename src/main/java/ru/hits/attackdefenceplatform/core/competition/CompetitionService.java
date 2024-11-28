package ru.hits.attackdefenceplatform.core.competition;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionAction;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

public interface CompetitionService {
    void changeCompetitionStatus(CompetitionAction action);
    CompetitionDto updateCompetition(UpdateCompetitionRequest request);
    CompetitionDto getCompetitionDto();
    Competition getCompetition();
}
