package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionAction;

public record ChangeStatusRequest(
        CompetitionAction action
)  {
}
