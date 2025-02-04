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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.core.user.mapper.UserMapper.mapUserEntityToMemberDto;
import static ru.hits.attackdefenceplatform.util.ColorUtils.generateRandomColor;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CompetitionService competitionService;
    private final VirtualMachineService virtualMachineService;
    private final PointsService pointsService;

    @Transactional
    @Override
    public TeamListDto createTeam(CreateTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
        team.setColor(generateRandomColor());
        var newTeam = teamRepository.save(team);

        return mapTeamEntityToTeamListDto(newTeam, null);
    }

    @Transactional
    @Override
    public void deleteTeam(UUID id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void joinToTeam(UserEntity user, UUID teamId) {
        var competition = competitionService.getCompetition();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете зайти в команду после начала соревнования");
        }

        var team = teamRepository.findByIdWithMembers(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        if (team.getMembers().stream().anyMatch(member -> member.getUser().equals(user))) {
            throw new UserException("Пользователь уже состоит в другой команде");
        }

        if (team.getMembers().size() >= team.getMaxMembers()) {
            throw new TeamException("В команде нет места для нового участника");
        }

        var teamMember = new TeamMemberEntity();
        teamMember.setUser(user);
        teamMember.setTeam(team);
        teamMemberRepository.save(teamMember);
    }

    @Transactional
    @Override
    public void leftFromTeam(UserEntity user, UUID teamId) {
        var competition = competitionService.getCompetition();
        if (competition.getStatus() != CompetitionStatus.NEW) {
            throw new TeamException("Вы не можете выйти из команды после начала соревнования");
        }

        var team = teamRepository.findByIdWithMembers(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var teamMember = team.getMembers().stream()
                .filter(member -> member.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new UserException("Пользователь не состоит в команде"));

        teamMemberRepository.delete(teamMember);
    }

    @Transactional(readOnly = true)
    @Override
    public TeamInfoDto getTeamById(UUID teamId, UserEntity user) {
        var team = teamRepository.findByIdWithMembers(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var members = team.getMembers();
        var userCount = members.size();
        var membersCount = team.getMaxMembers();
        var memberList = members.stream()
                .map(member -> mapUserEntityToMemberDto(member.getUser(), member.getPoints()))
                .toList();

        var canJoin = canUserJoinTeam(user, team, userCount);
        var isMyTeam = isUserInTeam(user, members);
        var canLeave = canLeaveFromTeam(user, members);
        var place = calculateTeamPlace(team);
        var points = pointsService.calculateTeamFlagPoints(team);
        var virtualMachine = getFullTeamVirtualMachineInfo(teamId, isMyTeam);

        return new TeamInfoDto(
                team.getId(), team.getName(), userCount, membersCount,
                place, points, canJoin, isMyTeam, canLeave, memberList, virtualMachine
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<TeamListDto> getAllTeams(UserEntity user) {
        return teamRepository.findAllWithMembers().stream()
                .map(team -> mapTeamEntityToTeamListDto(team, user))
                .sorted(Comparator.comparing(TeamListDto::name))
                .toList();
    }

    @Transactional
    @Override
    public List<TeamListDto> createManyTeams(CreateManyTeamsRequest request) {
        List<TeamListDto> teamListDtos = new ArrayList<>();
        for (long i = 1; i <= request.teamsCount(); i++) {
            var team = new TeamEntity();
            team.setName("Команда " + i);
            team.setMaxMembers(request.maxMembers());
            team.setColor(generateRandomColor());
            var newTeam = teamRepository.save(team);
            teamListDtos.add(mapTeamEntityToTeamListDto(newTeam, null));
        }
        return teamListDtos;
    }

    @Transactional
    @Override
    public void updateTeam(UUID teamId, CreateTeamRequest request) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда не найдена"));

        Optional.ofNullable(request.name()).filter(name -> !name.isBlank()).ifPresent(team::setName);
        Optional.ofNullable(request.maxMembers()).ifPresent(team::setMaxMembers);

        teamRepository.save(team);
    }

    @Transactional
    @Override
    public void removeMemberFromTeam(UUID teamId, UUID userId) {
        var teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UserException("Участник не найден в команде"));

        teamMemberRepository.delete(teamMember);
    }

    private boolean canUserJoinTeam(UserEntity user, TeamEntity team, long userCount) {
        var competition = competitionService.getCompetition();
        boolean competitionNotStarted = competition.getStatus() == CompetitionStatus.NEW;
        boolean isUserInTeam = team.getMembers().stream().anyMatch(member -> member.getUser().equals(user));

        return !isUserInTeam && userCount < team.getMaxMembers() && competitionNotStarted;
    }

    private boolean isUserInTeam(UserEntity user, List<TeamMemberEntity> members) {
        return members.stream().anyMatch(member -> member.getUser().equals(user));
    }

    private boolean canLeaveFromTeam(UserEntity user, List<TeamMemberEntity> members) {
        var competition = competitionService.getCompetition();
        return isUserInTeam(user, members) && competition.getStatus() == CompetitionStatus.NEW;
    }

    private VirtualMachineDto getFullTeamVirtualMachineInfo(UUID teamId, boolean isMyTeam) {
        var competition = competitionService.getCompetition();
        boolean competitionStarted = competition.getStatus() != CompetitionStatus.NEW;

        if (competitionStarted && isMyTeam) {
            return virtualMachineService.getVirtualMachinesByTeam(teamId).stream().findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public TeamListDto mapTeamEntityToTeamListDto(TeamEntity team, UserEntity user) {
        var userCount = team.getMembers().size();
        var membersCount = team.getMaxMembers();
        var isMyTeam = user != null && isUserInTeam(user, team.getMembers());
        var place = calculateTeamPlace(team);
        var points = pointsService.calculateTeamFlagPoints(team);

        var virtualMachineIp = Optional.ofNullable(getFullTeamVirtualMachineInfo(team.getId(), true))
                .map(VirtualMachineDto::ipAddress)
                .orElse(null);

        return new TeamListDto(team.getId(), team.getName(), place, points, userCount, membersCount, isMyTeam, virtualMachineIp);
    }

    //TODO: Что-то сделать с этим, это не дело
    private Integer calculateTeamPlace(TeamEntity team) {
        List<TeamEntity> allTeams = teamRepository.findAll();
        allTeams.sort((t1, t2) -> Integer.compare(calculateTeamPoints(t2), calculateTeamPoints(t1)));
        return allTeams.indexOf(team) + 1;
    }

    private Integer calculateTeamPoints(TeamEntity team) {
        return teamMemberRepository.findByTeam(team).stream()
                .mapToInt(member -> member.getPoints() != null ? member.getPoints() : 0)
                .sum();
    }
}



