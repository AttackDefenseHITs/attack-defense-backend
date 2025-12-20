package ru.hits.attackdefenceplatform.core.competition.mapper;

import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionShortDto;

public class CompetitionMapper {
    public static CompetitionDto mapToCompetitionDto(Competition competition){
        return new CompetitionDto(
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
                competition.getCompetitionMode().name()
        );
    }

    public static CompetitionShortDto mapToCompetitionShortDto(Competition competition){
        return new CompetitionShortDto(
                competition.getName(),
                competition.getStatus(),
                competition.getStartDate(),
                competition.getEndDate(),
                competition.getRoundDurationMinutes(),
                competition.getCurrentRound(),
                competition.getRules(),
                competition.getCompetitionMode().name()
        );
    }
}
