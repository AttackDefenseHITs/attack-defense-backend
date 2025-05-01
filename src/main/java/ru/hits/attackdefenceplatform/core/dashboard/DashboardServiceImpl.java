package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.dashboard.TeamScoreChangeDto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Реализация сервиса для работы с дашбордом, включая фильтрацию сабмита флагов и расчет изменений счета команд.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final FlagSubmissionRepository flagSubmissionRepository;
    private final CompetitionService competitionService;
    private final TeamRepository teamRepository;

    /**
     * Возвращает список изменений счета команд на основе сабмитов флагов, отфильтрованных по корректности и идентификатору команды.
     *
     * @param isCorrect флаг, указывающий, следует ли фильтровать сабмиты по корректности (true - корректные, false - некорректные)
     * @param teamId идентификатор команды, для которой необходимо получить сабмиты; если null, возвращаются сабмиты для всех команд
     * @return список DTO, описывающих изменения счета команд
     */
    @Override
    public List<TeamScoreChangeDto> getFilteredSubmissions(Boolean isCorrect, UUID teamId) {
        Specification<FlagSubmissionEntity> spec = FlagSubmissionSpecifications.createSpecification(isCorrect, teamId);
        var submissions = flagSubmissionRepository.findAll(spec);
        return convertSubmissionsToDTO(submissions);
    }

    /**
     * Преобразует список сущностей сабмитов флагов в список DTO, отражающих изменения счета команд.
     *
     * <p>Для каждого сабмита вычисляются очки, заработанные командой, которая отправила флаг, и очки, потерянные командой-владельцем флага.</p>
     * <p>Также инициализируются команды, отсутствующие в карте очков со значением равным нулю.</p>
     *
     * @param submissions список сущностей сабмитов флагов
     * @return список DTO с информацией по изменению счета команды
     */
    private List<TeamScoreChangeDto> convertSubmissionsToDTO(List<FlagSubmissionEntity> submissions) {
        Map<String, Integer> teamPointsMap = new HashMap<>();
        List<TeamScoreChangeDto> result = new ArrayList<>();

        for (FlagSubmissionEntity submission : submissions) {
            var submittingTeam = submission.getTeam();
            var submittingTeamName = submittingTeam.getName();
            var submittingTeamColor = submittingTeam.getColor();

            int pointsEarned = calculatePointsEarned(submission, submittingTeamName);
            int pointsLost = calculatePointsLost(submission, submittingTeamName);

            updateTeamPoints(submittingTeamName, pointsEarned, teamPointsMap);
            updateTeamPoints(submission.getFlag().getFlagOwner().getName(), pointsLost, teamPointsMap);

            result.add(createScoreChangeDto(
                    submittingTeamName,
                    submittingTeamColor,
                    submission.getSubmissionTime(),
                    pointsEarned,
                    teamPointsMap)
            );

            result.add(createScoreChangeDto(
                    submission.getFlag().getFlagOwner().getName(),
                    submission.getFlag().getFlagOwner().getColor(),
                    submission.getSubmissionTime(),
                    -pointsLost,
                    teamPointsMap)
            );
        }

        var startLocalTime = competitionService.getCompetitionDto().startDate();
        if (startLocalTime != null) {
            var startTime = Timestamp.valueOf(startLocalTime);
            initializeTeamsWithZeroPoints(teamPointsMap, result, startTime);
        }

        return result;
    }

    /**
     * Инициализирует в карте команд те команды, которые отсутствуют, устанавливая для них 0 баллов,
     * и добавляет соответствующие DTO с изменением счета с нулевыми значениями.
     *
     * @param teamPointsMap карта, содержащая текущие баллы команд
     * @param result список DTO, который будет дополнен информацией по командам без сабмитов
     * @param startTime время начала соревнования в виде Timestamp
     */
    private void initializeTeamsWithZeroPoints(Map<String, Integer> teamPointsMap,
                                               List<TeamScoreChangeDto> result,
                                               Date startTime) {
        var allTeams = teamRepository.findAll();
        for (TeamEntity team : allTeams) {
            String teamName = team.getName();
            if (!teamPointsMap.containsKey(teamName)) {
                teamPointsMap.put(teamName, 0);
            }
            result.add(new TeamScoreChangeDto(teamName, startTime, 0, 0, team.getColor()));
        }
    }

    /**
     * Вычисляет количество баллов, заработанных отправляющей командой по сабмиту.
     *
     * <p>Если сабмит корректный и флаг существует, и отправляющая команда не совпадает с владельцем флага,
     * возвращается стоимость отправки флага согласно данным соревнования.</p>
     *
     * @param submission сущность сабмита флага
     * @param submittingTeam имя отправляющей команды
     * @return количество заработанных баллов, либо 0 если условия не соблюдены
     */
    private int calculatePointsEarned(FlagSubmissionEntity submission, String submittingTeam) {
        var competitionDto = competitionService.getCompetitionDto();
        int pointsEarned = 0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsEarned = competitionDto.flagSendCost();
            }
        }
        return pointsEarned;
    }

    /**
     * Вычисляет количество баллов, потерянных владельцем флага по сабмиту.
     *
     * <p>Если сабмит корректный и флаг существует, и отправляющая команда не совпадает с владельцем флага,
     * возвращается отрицательное значение стоимости потери флага согласно данным соревнования.</p>
     *
     * @param submission сущность сабмита флага
     * @param submittingTeam имя отправляющей команды
     * @return количество потерянных баллов, либо 0 если условия не соблюдены
     */
    private int calculatePointsLost(FlagSubmissionEntity submission, String submittingTeam) {
        var competitionDto = competitionService.getCompetitionDto();
        int pointsLost = 0;
        if (submission.getIsCorrect() && submission.getFlag() != null) {
            String flagOwnerTeam = submission.getFlag().getFlagOwner().getName();
            if (!submittingTeam.equals(flagOwnerTeam)) {
                pointsLost = -competitionDto.flagLostCost();
            }
        }
        return pointsLost;
    }

    /**
     * Обновляет счет команды в карте баллов.
     *
     * <p>Если для заданной команды уже присутствует значение баллов, то к нему прибавляется указанное количество;
     * иначе команда добавляется в карту с начальным значением.</p>
     *
     * @param teamName имя команды
     * @param points количество баллов для добавления (или вычитания)
     * @param teamPointsMap карта, ассоциирующая имена команд с их баллами
     */
    private void updateTeamPoints(String teamName, int points, Map<String, Integer> teamPointsMap) {
        teamPointsMap.put(teamName, teamPointsMap.getOrDefault(teamName, 0) + points);
    }

    /**
     * Создает объект DTO, описывающий изменение счета для команды.
     *
     * @param teamName имя команды
     * @param teamColor цвет команды
     * @param time время сабмита
     * @param points изменение баллов (может быть положительным или отрицательным)
     * @param teamPointsMap карта, содержащая накопленные баллы для команды
     * @return объект TeamScoreChangeDto, содержащий данные по изменению счета команды
     */
    private TeamScoreChangeDto createScoreChangeDto(
            String teamName,
            String teamColor,
            Date time,
            int points,
            Map<String, Integer> teamPointsMap
    ) {
        return new TeamScoreChangeDto(
                teamName,
                time,
                points,
                teamPointsMap.get(teamName),
                teamColor
        );
    }
}


