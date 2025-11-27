package ru.hits.attackdefenceplatform.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionMode;
import ru.hits.attackdefenceplatform.business.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.business.competition.mapper.CompetitionMapper;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.repository.CompetitionRepository;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;

@Component
@RequiredArgsConstructor
public class CompetitionContext {
    private final CompetitionRepository competitionRepository;

    public Competition getCurrent() {
        return competitionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CompetitionException("Нет активного соревнования"));
    }

    public CompetitionDto getCompetitionDto() {
        return CompetitionMapper.mapToCompetitionDto(getCurrent());
    }

    public boolean isInNew() {
        return getCurrent().getStatus() == CompetitionStatus.NEW;
    }

    public boolean isInProgress() {
        return getCurrent().getStatus() == CompetitionStatus.IN_PROGRESS;
    }

    public boolean currentRoundIsZero(){
        return getCurrent().getCurrentRound() == 0;
    }

    public CompetitionMode getMode() {
        return getCurrent().getCompetitionMode();
    }
}

