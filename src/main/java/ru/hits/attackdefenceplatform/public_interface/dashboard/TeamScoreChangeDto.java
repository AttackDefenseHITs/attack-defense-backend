package ru.hits.attackdefenceplatform.public_interface.dashboard;

import java.util.Date;
import java.util.UUID;

public record TeamScoreChangeDto(
        String teamName,
        Date submissionTime,
        Integer pointsEarned,
        Integer totalTeamPoints
) {}

