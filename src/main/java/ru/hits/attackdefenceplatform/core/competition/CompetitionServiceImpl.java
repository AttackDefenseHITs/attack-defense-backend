package ru.hits.attackdefenceplatform.core.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper;
import ru.hits.attackdefenceplatform.core.competition.repository.Competition;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionAction;
import ru.hits.attackdefenceplatform.core.competition.repository.CompetitionRepository;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;
import ru.hits.attackdefenceplatform.websocket.model.NotificationEventModel;
import ru.hits.attackdefenceplatform.websocket.storage.key.WebSocketHandlerType;
import ru.hits.attackdefenceplatform.public_interface.competition.CompetitionDto;
import ru.hits.attackdefenceplatform.public_interface.competition.UpdateCompetitionRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.core.competition.mapper.CompetitionMapper.mapToCompetitionDto;

/**
 * Сервис для управления процессом прохождения соревнования
 */
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final WebSocketClient<NotificationEventModel> notificationWebSocketClient;

    /**
     * Метод для изменения статуса соревнования
     */
    @Override
    @Transactional
    public CompetitionDto changeCompetitionStatus(CompetitionAction action) {
        var competition = getCompetition();

        switch (action) {
            case START -> handleStartCompetition(competition);
            case COMPLETE -> handleCompleteCompetition(competition);
            case CANCEL -> handleCancelCompetition(competition);
            case PAUSE -> handlePauseCompetition(competition);
            case RESUME -> handleResumeCompetition(competition);
            default -> throw new CompetitionException("Неизвестное действие: " + action);
        }

        var updatedCompetition = competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(updatedCompetition);
    }

    /**
     * Обработка старта соревнования
     */
    private void handleStartCompetition(Competition competition) {
        if (competition.getStatus() != CompetitionStatus.NEW &&
                competition.getStatus() != CompetitionStatus.CANCELLED &&
                competition.getStatus() != CompetitionStatus.COMPLETED) {
            throw new CompetitionException("Соревнование может быть запущено только из состояния NEW, CANCELLED или COMPLETED");
        }
        competition.setStartDate(LocalDateTime.now(ZoneOffset.UTC));
        competition.setStatus(CompetitionStatus.IN_PROGRESS);
        competition.setCurrentRound(0);
        notifyParticipants("Соревнование началось! Удачи!");
    }

    /**
     * Обработка завершения соревнования
     */
    private void handleCompleteCompetition(Competition competition) {
        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new CompetitionException("Соревнование может быть завершено только из состояния IN_PROGRESS");
        }
        competition.setStatus(CompetitionStatus.COMPLETED);

        notifyParticipants("Соревнование завершено! Всем спасибо за участие!");
    }

    /**
     * Обработка отмены соревнования
     */
    private void handleCancelCompetition(Competition competition) {
        if (competition.getStatus() == CompetitionStatus.NEW) {
            throw new CompetitionException("Соревнование в статусе NEW не может быть отменено");
        }
        if (competition.getStatus() == CompetitionStatus.COMPLETED ||
                competition.getStatus() == CompetitionStatus.CANCELLED) {
            throw new CompetitionException("Соревнование не может быть отменено, так как оно уже завершено или отменено");
        }
        competition.setStatus(CompetitionStatus.CANCELLED);

        notifyParticipants("Соревнование было отменено!");
    }

    /**
     * Обработка паузы соревнования
     */
    private void handlePauseCompetition(Competition competition) {
        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new CompetitionException("Соревнование может быть поставлено на паузу только из состояния IN_PROGRESS");
        }
        competition.setStatus(CompetitionStatus.PAUSED);

        notifyParticipants("Соревнование было приостановлено!");
    }

    /**
     * Обработка возобновления соревнования
     */
    private void handleResumeCompetition(Competition competition) {
        if (competition.getStatus() != CompetitionStatus.PAUSED) {
            throw new CompetitionException("Соревнование может быть возобновлено только из состояния PAUSED");
        }
        competition.setStatus(CompetitionStatus.IN_PROGRESS);

        notifyParticipants("Соревнование возобновлено!");
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

        competition.setName(request.name());
        competition.setStartDate(request.startDate());
        competition.setEndDate(request.endDate());
        competition.setTotalRounds(request.totalRounds());
        competition.setRoundDurationMinutes(request.roundDurationMinutes());
        competition.setRules(request.rules());

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
    /**
     * Обновить соревнование
     */
    @Override
    public CompetitionDto restartCompetition() {
        var competition = getCompetition();
        competition.setStatus(CompetitionStatus.NEW);
        competition.setTotalRounds(5);
        competition.setRoundDurationMinutes(20);
        competition.setStartDate(null);
        competition.setEndDate(null);
        competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(competition);
    }

    /**
     * Начало следующего раунда.
     */
    @Transactional
    public CompetitionDto startNextRound() {
        var competition = getCompetition();

        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new CompetitionException("Нельзя начинать новый раунд, если соревнование не в состоянии IN_PROGRESS");
        }

        if (competition.getCurrentRound() >= competition.getTotalRounds()) {
            throw new CompetitionException("Все раунды уже завершены");
        }

        competition.setCurrentRound(competition.getCurrentRound() + 1);

        notifyParticipants("Начался раунд " + competition.getCurrentRound());

        competitionRepository.save(competition);
        return CompetitionMapper.mapToCompetitionDto(competition);
    }

    /**
     * Получение информации о текущем раунде.
     */
    @Transactional(readOnly = true)
    public Integer getCurrentRound() {
        var competition = getCompetition();
        return competition.getCurrentRound();
    }

    /**
     * Уведомление участников о начале соревнования
     */
    private void notifyParticipants(String message) {
        var participantIds = getAllParticipantIds();
        var eventMessage = new NotificationEventModel(WebSocketHandlerType.EVENT, message);
        notificationWebSocketClient.sendNotification(eventMessage, participantIds);
    }

    /**
     * Получить список идентификаторов участников
     */
    private List<String> getAllParticipantIds() {
        return teamMemberRepository.findAllUserIds().stream()
                .map(UUID::toString)
                .toList();
    }
}
