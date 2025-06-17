package ru.hits.attackdefenceplatform.public_interface.dashboard;

import java.util.Date;

public record FlagSubmissionDto(
        String submittedFlag,
        String name,
        Date submissionTime,
        Boolean isCorrect
) {
}
