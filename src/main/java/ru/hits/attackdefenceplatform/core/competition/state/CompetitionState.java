package ru.hits.attackdefenceplatform.core.competition.state;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;

public class CompetitionState {
    public static Competition getDefaultCompetitionState(){
        var competition = new Competition();
        competition.setId(1L);
        competition.setName("Attack-defense");
        competition.setStatus(CompetitionStatus.NEW);

        return competition;
    }
}
