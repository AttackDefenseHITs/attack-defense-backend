package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionMode;

public record UpdateCompetitionModeRequest(
   CompetitionMode competitionMode
) {}
