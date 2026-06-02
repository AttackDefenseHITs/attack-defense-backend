package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;

public record ChangeStatusRequest(
        CompetitionAction action
)  {
}
