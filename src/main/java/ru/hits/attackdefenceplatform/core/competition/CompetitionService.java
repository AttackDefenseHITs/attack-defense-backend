package ru.hits.attackdefenceplatform.core.competition;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

public interface CompetitionService {
    void startCompetition();
    void completeCompetition();
    void cancelCompetition();
    void pauseCompetition();
    void resumeCompetition();
    void resetCompetition();
    CompetitionDto updateCompetition(UpdateCompetitionRequest request);
    CompetitionDto getCompetitionDto();
    Competition getCompetition();
}
