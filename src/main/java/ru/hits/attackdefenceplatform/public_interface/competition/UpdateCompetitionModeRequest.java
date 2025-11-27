package ru.hits.attackdefenceplatform.public_interface.competition;

import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionMode;

public record UpdateCompetitionModeRequest(
   CompetitionMode competitionMode
) {}
