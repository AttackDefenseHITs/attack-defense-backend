package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionRepository;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionStatus;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper.mapToCompetitionDto;

/**
 * Сервис для управления процессом прохождения соревнования
 */
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {
    private final CompetitionRepository competitionRepository;

    /**
     * Метод для изменения статуса соревнования
     */
    @Override
    @Transactional
    public CompetitionDto changeCompetitionStatus(CompetitionAction action) {
        var competition = getCompetition();

        switch (action) {
            case START -> {
                if (competition.getStatus() != CompetitionStatus.NEW &&
                        competition.getStatus() != CompetitionStatus.CANCELLED &&
                        competition.getStatus() != CompetitionStatus.COMPLETED) {
                    throw new CompetitionException("Соревнование может быть запущено только из состояния NEW, CANCELLED или COMPLETED");
                }
                competition.setStatus(CompetitionStatus.IN_PROGRESS);
                competition.setStartDate(LocalDateTime.now());
            }
            case COMPLETE -> {
                if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
                    throw new CompetitionException("Соревнование может быть завершено только из состояния IN_PROGRESS");
                }
                competition.setStatus(CompetitionStatus.COMPLETED);
                competition.setEndDate(LocalDateTime.now());
            }
            case CANCEL -> {
                if (competition.getStatus() == CompetitionStatus.NEW) {
                    throw new CompetitionException("Соревнование в статусе NEW не может быть отменено");
                }
                if (competition.getStatus() == CompetitionStatus.COMPLETED ||
                        competition.getStatus() == CompetitionStatus.CANCELLED) {
                    throw new CompetitionException("Соревнование не может быть отменено, так как оно уже завершено или отменено");
                }
                competition.setStatus(CompetitionStatus.CANCELLED);
            }
            case PAUSE -> {
                if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
                    throw new CompetitionException("Соревнование может быть поставлено на паузу только из состояния IN_PROGRESS");
                }
                competition.setStatus(CompetitionStatus.PAUSED);
            }
            case RESUME -> {
                if (competition.getStatus() != CompetitionStatus.PAUSED) {
                    throw new CompetitionException("Соревнование может быть возобновлено только из состояния PAUSED");
                }
                competition.setStatus(CompetitionStatus.IN_PROGRESS);
            }
            default -> throw new CompetitionException("Неизвестное действие: " + action);
        }

        var updatedCompetition = competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(updatedCompetition);
    }

    /**
     * Получить возможные действия в зависимости от текущего статуса соревнования
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompetitionAction> getAvailableActions() {
        var competition = getCompetition();
        var currentStatus = competition.getStatus();

        return switch (currentStatus) {
            case NEW, CANCELLED, COMPLETED -> List.of(CompetitionAction.START);
            case IN_PROGRESS -> List.of(CompetitionAction.COMPLETE, CompetitionAction.PAUSE, CompetitionAction.CANCEL);
            case PAUSED -> List.of(CompetitionAction.RESUME, CompetitionAction.CANCEL);
        };
    }

    /**
     * Метод для обновления настроек соревнования
     */
    @Override
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

        var updatedCompetition = competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(updatedCompetition);
    }

    /**
     * Получить информацию о текущем соревновании в формате DTO
     */
    @Override
    @Transactional(readOnly = true)
    public CompetitionDto getCompetitionDto() {
        var competition = getCompetition();
        return mapToCompetitionDto(competition);
    }

    /**
     * Получить сущность соревнования (он всегда один)
     */
    @Override
    @Transactional(readOnly = true)
    public Competition getCompetition() {
        return competitionRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new CompetitionException("Соревнование не найдено"));
    }
}
