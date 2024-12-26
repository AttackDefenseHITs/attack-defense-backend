package ru.hits.attackdefenceplatform.public_interface.dashboard;

import java.util.Date;
import java.util.UUID;

public record FlagSubmissionWithPointsDto(
        UUID submissionId,
        String teamName,
        String teamMemberName,
        String submittedFlag,
        Date submissionTime,
        Boolean isCorrect,
        Integer pointsEarned,
        Integer totalTeamPoints
) {}

