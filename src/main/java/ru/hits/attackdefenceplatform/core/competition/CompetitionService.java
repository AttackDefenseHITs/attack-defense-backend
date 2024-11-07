package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionRepository;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper.mapToCompetitionDto;

/**
 * Сервис для управления процессом прохождения соревнования
 */
@Service
@RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository competitionRepository;

    /**
     * Запустить соревнование, изменив статус на IN_PROGRESS.
     */
    @Transactional
    public void startCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new RuntimeException("Соревнование может быть запущено только из состояния NEW");
        }

        competition.setStatus(CompetitionStatus.IN_PROGRESS);
        competition.setStartDate(LocalDateTime.now());
        competitionRepository.save(competition);
    }

    /**
     * Завершить соревнование, изменив статус на COMPLETED.
     */
    @Transactional
    public void completeCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new RuntimeException("Соревнование может быть завершено только из состояния IN_PROGRESS");
        }

        competition.setStatus(CompetitionStatus.COMPLETED);
        competition.setEndDate(LocalDateTime.now());
        competitionRepository.save(competition);
    }

    /**
     * Отменить соревнование, изменив статус на CANCELLED.
     */
    @Transactional
    public void cancelCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() == CompetitionStatus.COMPLETED ||
                competition.getStatus() == CompetitionStatus.CANCELLED) {
            throw new RuntimeException("Соревнование не может быть отменено, так как оно уже завершено или отменено");
        }

        competition.setStatus(CompetitionStatus.CANCELLED);
        competitionRepository.save(competition);
    }

    /**
     * Поставить соревнование на паузу, изменив статус на PAUSED.
     */
    @Transactional
    public void pauseCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new RuntimeException("Соревнование может быть поставлено на паузу только из состояния IN_PROGRESS");
        }

        competition.setStatus(CompetitionStatus.PAUSED);
        competitionRepository.save(competition);
    }

    /**
     * Возобновить соревнование, изменив статус обратно на IN_PROGRESS.
     */
    @Transactional
    public void resumeCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.PAUSED) {
            throw new RuntimeException("Соревнование может быть возобновлено только из состояния PAUSED");
        }

        competition.setStatus(CompetitionStatus.IN_PROGRESS);
        competitionRepository.save(competition);
    }

    /**
     * Сбросить статус соревнования на NEW.
     */
    @Transactional
    public void resetCompetition() {
        var competition = getCompetition();

        if (competition.getStatus() == CompetitionStatus.NEW) {
            throw new RuntimeException("Соревнование уже находится в состоянии NEW");
        }

        competition.setStatus(CompetitionStatus.NEW);
        competition.setStartDate(null);
        competition.setEndDate(null);
        competitionRepository.save(competition);
    }

    /**
     * Метод для обновления настроек соревнования
     */
    @Transactional
    public CompetitionDto updateCompetition(UpdateCompetitionRequest request) {
        var competition = getCompetition();

        Optional.ofNullable(request.name())
                .filter(name -> !name.isBlank())
                .ifPresent(competition::setName);

        Optional.ofNullable(request.startDate())
                .ifPresent(competition::setStartDate);

        Optional.ofNullable(request.endDate())
                .ifPresent(competition::setEndDate);

        Optional.ofNullable(request.durationMinutes())
                .ifPresent(competition::setDurationMinutes);

        Optional.ofNullable(request.rules())
                .filter(rules -> !rules.isBlank())
                .ifPresent(competition::setRules);

        var newCompetition = competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(newCompetition);
    }

    /**
     * Получить информацию о текущем соревновании в формате DTO
     */
    @Transactional(readOnly = true)
    public CompetitionDto getCompetitionDto() {
        var competition = getCompetition();
        return mapToCompetitionDto(competition);
    }

    /**
     * Получить сущность соревнования (он всегда один)
     */
    @Transactional(readOnly = true)
    public Competition getCompetition() {
        return competitionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Соревнование не найдено"));
    }
}
