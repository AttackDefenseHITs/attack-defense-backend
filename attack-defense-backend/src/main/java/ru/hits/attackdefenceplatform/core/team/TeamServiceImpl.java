package ru.hits.attackdefenceplatform.core.team;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.common.exception.UserException;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.points.PointsService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.model.TeamPointsDto;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.virtual_machine.VirtualMachineService;
import ru.hits.attackdefenceplatform.core.CompetitionContext;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamShortDataDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserTeamMemberDto;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;
import ru.hits.attackdefenceplatform.util.NumberUtils;

import java.util.Comparator;
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
    private final CompetitionContext context;

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VirtualMachineService virtualMachineService;
    private final PointsService pointsService;

    private record TeamRatingRow(
            UUID teamId,
            Double points
    ) {}

    @Transactional
    @Override
    public void joinToTeam(UserEntity user, UUID teamId) {
        var competition = context.getCurrent();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете зайти в команду после начала соревнования");
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        if (Boolean.TRUE.equals(team.getIsSystem())) {
            throw new TeamException("Нельзя вступить в системную команду");
        }

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

    @Transactional
    @Override
    public void leftFromTeam(UserEntity user, UUID teamId) {
        var competition = context.getCurrent();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете выйти из команды после начала соревнования");
        }

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        if (Boolean.TRUE.equals(team.getIsSystem())) {
            throw new TeamException("Нельзя выйти из системной команды таким способом");
        }

        var teamMember = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UserException("Пользователь не состоит в команде с ID " + teamId));

        teamMemberRepository.delete(teamMember);
    }

    @Transactional(readOnly = true)
    @Override
    public TeamInfoDto getTeamById(UUID teamId, UserEntity user) {
        var team = teamRepository.findById(teamId)
                .filter(t -> !Boolean.TRUE.equals(t.getIsSystem()))
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();

        var memberList = teamMemberRepository.findByTeam(team).stream()
                .filter(member -> !Boolean.TRUE.equals(member.getUser().getIsSystem()))
                .map(member -> mapUserEntityToMemberDto(member.getUser(), member.getPoints()))
                .toList();

        List<TeamRatingRow> rankedTeams = getRankedTeamsCorrectly();

        var canJoin = canUserJoinTeam(user, team);
        var isMyTeam = isUserInTeam(user, team);
        var canLeave = canLeaveFromTeam(user, team);

        Integer place = calculateTeamPlace(team, rankedTeams);
        Double points = NumberUtils.roundToThreeDecimals(calculateTeamPoints(team));
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

    @Transactional(readOnly = true)
    @Override
    public List<TeamListDto> getAllTeams(UserEntity user) {
        List<TeamRatingRow> rankedTeams = getRankedTeamsCorrectly();

        return teamRepository.findAllByIsSystemFalse().stream()
                .map(team -> mapTeamEntityToTeamListDto(team, user, rankedTeams))
                .toList();
    }

    @Transactional
    @Override
    public void removeMemberFromTeam(UUID teamId, UUID userId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Команда не найдена"));

        if (Boolean.TRUE.equals(team.getIsSystem())) {
            throw new TeamException("Нельзя удалять участников из системной команды");
        }

        var teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UserException("Участник с ID " + userId + " не найден в команде с ID " + teamId));
        teamMemberRepository.delete(teamMember);
    }

    private boolean canUserJoinTeam(UserEntity user, TeamEntity team) {
        if (Boolean.TRUE.equals(team.getIsSystem())) {
            return false;
        }

        boolean isUserInTeam = teamMemberRepository.existsByUser(user);
        long userCount = teamMemberRepository.countByTeam(team);
        var competition = context.getCurrent();
        boolean competitionNotStarted = competition.getStatus().equals(CompetitionStatus.NEW);
        return !isUserInTeam && userCount < team.getMaxMembers() && competitionNotStarted;
    }

    private boolean isUserInTeam(UserEntity user, TeamEntity team) {
        return teamMemberRepository.existsByUserAndTeam(user, team);
    }

    private boolean canLeaveFromTeam(UserEntity user, TeamEntity team) {
        return !Boolean.TRUE.equals(team.getIsSystem()) && isUserInTeam(user, team) && context.isInNew();
    }

    private List<TeamRatingRow> getRankedTeamsCorrectly() {
        return teamRepository.findAllByIsSystemFalse().stream()
                .map(team -> new TeamRatingRow(
                        team.getId(),
                        Optional.ofNullable(pointsService.calculateTeamFlagPoints(team)).orElse(0.0)
                ))
                .sorted(Comparator.comparingDouble(TeamRatingRow::points).reversed())
                .toList();
    }

    public Integer calculateTeamPlace(TeamEntity team, List<TeamRatingRow> rankedTeams) {
        for (int i = 0; i < rankedTeams.size(); i++) {
            if (rankedTeams.get(i).teamId().equals(team.getId())) {
                return i + 1;
            }
        }
        return null;
    }

    public Double calculateTeamPoints(TeamEntity team) {
        return pointsService.calculateTeamFlagPoints(team);
    }

    private VirtualMachineDto getFullTeamVirtualMachineInfo(UUID teamId, boolean isMyTeam) {
        boolean competitionStarted = !context.isInNew();
        if (competitionStarted && isMyTeam) {
            return virtualMachineService.getVirtualMachinesByTeam(teamId)
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public TeamShortDataDto mapToTeamServiceStatusDto(TeamEntity team) {
        List<TeamRatingRow> rankedTeams = getRankedTeamsCorrectly();
        var place = calculateTeamPlace(team, rankedTeams);
        var points = calculateTeamPoints(team);
        var virtualMachineIp = Optional.ofNullable(getFullTeamVirtualMachineInfo(team.getId(), true))
                .map(VirtualMachineDto::ipAddress)
                .orElse(null);

        return new TeamShortDataDto(
                team.getId(),
                team.getName(),
                place,
                points,
                virtualMachineIp
        );
    }

    @Override
    public List<UserTeamMemberDto> getTeamMemberRatings() {
        return teamMemberRepository.findAll().stream()
                .filter(member -> !Boolean.TRUE.equals(member.getTeam().getIsSystem()))
                .filter(member -> !Boolean.TRUE.equals(member.getUser().getIsSystem()))
                .map(member -> mapUserEntityToMemberDto(member.getUser(), NumberUtils.roundToThreeDecimals(member.getPoints())))
                .sorted(Comparator.comparingDouble(UserTeamMemberDto::points).reversed())
                .toList();
    }

    private TeamListDto mapTeamEntityToTeamListDto(TeamEntity team, UserEntity user, List<TeamRatingRow> rankedTeams) {
        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();
        var isMyTeam = Optional.ofNullable(user)
                .map(u -> isUserInTeam(u, team))
                .orElse(false);
        var place = calculateTeamPlace(team, rankedTeams);
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
