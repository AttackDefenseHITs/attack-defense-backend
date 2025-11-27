package ru.hits.attackdefenceplatform.business.competition.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.repository.CompetitionRepository;

@Service
@Slf4j
public class AttackDefenseHandler extends CompetitionModeHandler {
    public AttackDefenseHandler(CompetitionRepository competitionRepository) {
        super(competitionRepository);
    }

    @Override
    public void onStart(Competition competition) {

    }

    @Override
    public void onNextRound(Competition competition) {

    }

    @Override
    public void onComplete(Competition competition) {

    }

    @Override
    public void onRestart(Competition competition) {

    }
}
