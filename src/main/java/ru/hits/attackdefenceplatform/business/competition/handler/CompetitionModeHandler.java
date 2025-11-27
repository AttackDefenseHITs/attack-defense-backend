package ru.hits.attackdefenceplatform.business.competition.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.business.competition.repository.Competition;
import ru.hits.attackdefenceplatform.business.competition.repository.CompetitionRepository;

@RequiredArgsConstructor
public abstract class CompetitionModeHandler {
    protected final CompetitionRepository competitionRepository;

    /**
     * Логика запуска соревнования
     */
    public abstract void onStart(Competition competition);

    /**
     * Логика следующего раунда
     */
    public abstract void onNextRound(Competition competition);

    /**
     * Логика завершения соревнования
     */
    public abstract void onComplete(Competition competition);

    /**
     * Логика перезагрузки соревнования
     */
    public abstract void onRestart(Competition competition);

    /**
     * Получить сущность соревнования (он всегда один)
     */
    @Transactional(readOnly = true)
    protected Competition getCompetition() {
        return competitionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CompetitionException("Соревнование не найдено"));
    }
}