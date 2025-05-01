package ru.hits.attackdefenceplatform.core.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.common.exception.UserException;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.points.PointsService;
import ru.hits.attackdefenceplatform.core.points.SlaService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.VirtualMachineService;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;
import ru.hits.attackdefenceplatform.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.core.user.mapper.UserMapper.mapUserEntityToMemberDto;

/**
 * Сервис для работы с командами в соревнованиях.
 */
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CompetitionService competitionService;
    private final VirtualMachineService virtualMachineService;
    private final PointsService pointsService;

    /**
     * Создает новую команду.
     *
     * @param request данные для создания команды
     * @return DTO новой команды
     */
    @Transactional
    @Override
    public TeamListDto createTeam(CreateTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
        team.setColor(ColorUtils.generateRandomColor());
        var newTeam = teamRepository.save(team);
        return mapTeamEntityToTeamListDto(newTeam, null);
    }

    /**
     * Удаляет команду по ID.
     *
     * @param id идентификатор команды
     */
    @Transactional
    @Override
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    /**
     * Добавляет пользователя в команду.
     *
     * @param user пользователь, желающий присоединиться
     * @param teamId идентификатор команды
     */
    @Transactional
    @Override
    public void joinToTeam(UserEntity user, UUID teamId) {
        var competition = competitionService.getCompetition();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете зайти в команду после начала соревнования");
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        if (teamMemberRepository.existsByUser(user)) {
            throw new UserException("Пользователь уже состоит в другой команде");
        }

        long currentMembersCount = teamMemberRepository.countByTeam(team);
        if (currentMembersCount >= team.getMaxMembers()) {
            throw new TeamException("В команде с ID " + teamId + " нет места для нового участника");
        }

        var teamMember = new TeamMemberEntity();
        teamMember.setUser(user);
        teamMember.setTeam(team);
        teamMemberRepository.save(teamMember);
    }

    /**
     * Удаляет пользователя из команды.
     *
     * @param user пользователь, покидающий команду
     * @param teamId идентификатор команды
     */
    @Transactional
    @Override
    public void leftFromTeam(UserEntity user, UUID teamId) {
        var competition = competitionService.getCompetition();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете выйти из команды после начала соревнования");
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var teamMember = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UserException("Пользователь не состоит в команде с ID " + teamId));

        teamMemberRepository.delete(teamMember);
    }

    /**
     * Возвращает информацию о команде.
     *
     * @param teamId идентификатор команды
     * @param user пользователь, запрашивающий информацию
     * @return DTO с информацией о команде
     */
    @Transactional(readOnly = true)
    @Override
    public TeamInfoDto getTeamById(UUID teamId, UserEntity user) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();

        var memberList = teamMemberRepository.findByTeam(team).stream()
                .map(member -> mapUserEntityToMemberDto(member.getUser(), member.getPoints()))
                .toList();

        var canJoin = canUserJoinTeam(user, team);
        var isMyTeam = isUserInTeam(user, team);
        var canLeave = canLeaveFromTeam(user, team);

        Integer place = calculateTeamPlace(team);
        Double points = calculateTeamPoints(team);
        var virtualMachine = getFullTeamVirtualMachineInfo(teamId, isMyTeam);

        return new TeamInfoDto(
                team.getId(),
                team.getName(),
                userCount,
                membersCount,
                place,
                points,
                canJoin,
                isMyTeam,
                canLeave,
                memberList,
                virtualMachine
        );
    }

    /**
     * Возвращает список всех команд с информацией для отображения.
     *
     * @param user пользователь, запрашивающий список
     * @return список DTO команд
     */
    @Transactional(readOnly = true)
    @Override
    public List<TeamListDto> getAllTeams(UserEntity user) {
        return teamRepository.findAll().stream()
                .map(team -> mapTeamEntityToTeamListDto(team, user))
                .toList();
    }

    /**
     * Создает множество команд.
     *
     * @param request данные для создания команд
     * @return список DTO созданных команд
     */
    @Transactional
    @Override
    public List<TeamListDto> createManyTeams(CreateManyTeamsRequest request) {
        List<TeamListDto> teamListDtos = new ArrayList<>();
        for (long i = 1; i <= request.teamsCount(); i++) {
            String teamName = "Команда " + i;
            var team = new TeamEntity();
            team.setName(teamName);
            team.setMaxMembers(request.maxMembers());
            var newTeam = teamRepository.save(team);
            teamListDtos.add(mapTeamEntityToTeamListDto(newTeam, null));
        }
        return teamListDtos;
    }

    /**
     * Обновляет данные команды.
     *
     * @param teamId идентификатор команды
     * @param request новые данные для команды
     */
    @Transactional
    @Override
    public void updateTeam(UUID teamId, CreateTeamRequest request) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        Optional.ofNullable(request.name())
                .filter(name -> !name.isBlank())
                .ifPresent(team::setName);

        Optional.ofNullable(request.maxMembers())
                .ifPresent(team::setMaxMembers);

        teamRepository.save(team);
    }

    /**
     * Удаляет участника из команды.
     *
     * @param teamId идентификатор команды
     * @param userId идентификатор пользователя
     */
    @Transactional
    @Override
    public void removeMemberFromTeam(UUID teamId, UUID userId) {
        var teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UserException("Участник с ID " + userId + " не найден в команде с ID " + teamId));
        teamMemberRepository.delete(teamMember);
    }

    /**
     * Проверяет, может ли пользователь присоединиться к команде.
     *
     * @param user пользователь
     * @param team команда
     * @return true, если возможно, иначе false
     */
    private boolean canUserJoinTeam(UserEntity user, TeamEntity team) {
        boolean isUserInTeam = teamMemberRepository.existsByUser(user);
        long userCount = teamMemberRepository.countByTeam(team);
        var competition = competitionService.getCompetition();
        boolean competitionNotStarted = competition.getStatus().equals(CompetitionStatus.NEW);
        return !isUserInTeam && userCount < team.getMaxMembers() && competitionNotStarted;
    }

    /**
     * Проверяет, состоит ли пользователь в команде.
     *
     * @param user пользователь
     * @param team команда
     * @return true, если пользователь состоит в команде, иначе false
     */
    private boolean isUserInTeam(UserEntity user, TeamEntity team) {
        return teamMemberRepository.existsByUserAndTeam(user, team);
    }

    /**
     * Проверяет, может ли пользователь покинуть команду.
     *
     * @param user пользователь
     * @param team команда
     * @return true, если пользователь может выйти, иначе false
     */
    private boolean canLeaveFromTeam(UserEntity user, TeamEntity team) {
        var competition = competitionService.getCompetition();
        return isUserInTeam(user, team) && competition.getStatus().equals(CompetitionStatus.NEW);
    }

    /**
     * Вычисляет место команды в рейтинге.
     *
     * @param team команда
     * @return место команды
     */
    private Integer calculateTeamPlace(TeamEntity team) {
        List<TeamEntity> allTeams = teamRepository.findAll();
        allTeams.sort((t1, t2) -> Double.compare(calculateTeamPoints(t2), calculateTeamPoints(t1)));
        return allTeams.indexOf(team) + 1;
    }

    /**
     * Вычисляет баллы команды.
     *
     * @param team команда
     * @return количество баллов
     */
    private Double calculateTeamPoints(TeamEntity team) {
        return pointsService.calculateTeamFlagPoints(team);
    }

    /**
     * Возвращает информацию о виртуальной машине команды.
     *
     * @param teamId идентификатор команды
     * @param isMyTeam флаг, указывающий, является ли запрос от участника команды
     * @return DTO виртуальной машины или null
     */
    private VirtualMachineDto getFullTeamVirtualMachineInfo(UUID teamId, boolean isMyTeam) {
        var competition = competitionService.getCompetition();
        boolean competitionStarted = !competition.getStatus().equals(CompetitionStatus.NEW);
        if (competitionStarted && isMyTeam) {
            return virtualMachineService.getVirtualMachinesByTeam(teamId)
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Преобразует сущность команды в DTO для списка.
     *
     * @param team сущность команды
     * @param user пользователь, запрашивающий информацию (может быть null)
     * @return DTO команды
     */
    @Override
    public TeamListDto mapTeamEntityToTeamListDto(TeamEntity team, UserEntity user) {
        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();
        var isMyTeam = Optional.ofNullable(user)
                .map(u -> isUserInTeam(u, team))
                .orElse(false);
        var place = calculateTeamPlace(team);
        var points = calculateTeamPoints(team);
        var virtualMachineIp = Optional.ofNullable(getFullTeamVirtualMachineInfo(team.getId(), true))
                .map(VirtualMachineDto::ipAddress)
                .orElse(null);
        return new TeamListDto(
                team.getId(),
                team.getName(),
                place,
                points,
                userCount,
                membersCount,
                isMyTeam,
                virtualMachineIp
        );
    }
}
