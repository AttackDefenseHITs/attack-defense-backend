package ru.hits.attackdefenceplatform.core.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.common.exception.UserException;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.core.user.mapper.UserMapper.mapUserEntityToMemberDto;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CompetitionService competitionService;

    @Transactional
    @Override
    public TeamListDto createTeam(CreateTeamRequest request) {
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
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

        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        boolean isUserInAnyTeam = teamMemberRepository.existsByUser(user);
        if (isUserInAnyTeam) {
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
        Integer points = calculateTeamPoints(team);

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
                memberList
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<TeamListDto> getAllTeams(UserEntity user) {
        return teamRepository.findAll().stream()
                .map(team -> mapTeamEntityToTeamListDto(team, user))
                .toList();
    }

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

    @Transactional
    @Override
    public void removeMemberFromTeam(UUID teamId, UUID userId) {
        var teamMember = teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new UserException("Участник с ID " + userId + " не найден в команде с ID " + teamId));

        teamMemberRepository.delete(teamMember);
    }

    private boolean canUserJoinTeam(UserEntity user, TeamEntity team) {
        boolean isUserInTeam = teamMemberRepository.existsByUser(user);
        long userCount = teamMemberRepository.countByTeam(team);
        var competition = competitionService.getCompetition();
        boolean competitionNotStarted = competition.getStatus().equals(CompetitionStatus.NEW);

        return !isUserInTeam && userCount < team.getMaxMembers() && competitionNotStarted;
    }

    private boolean isUserInTeam(UserEntity user, TeamEntity team) {
        return teamMemberRepository.existsByUserAndTeam(user, team);
    }

    private boolean canLeaveFromTeam(UserEntity user, TeamEntity team){
        var competition = competitionService.getCompetition();

        var userInThisTeam = isUserInTeam(user, team);
        var competitionNotStarted = competition.getStatus().equals(CompetitionStatus.NEW);

        return userInThisTeam && competitionNotStarted;
    }

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

    private TeamListDto mapTeamEntityToTeamListDto(TeamEntity team, UserEntity user) {
        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();

        var isMyTeam = Optional.ofNullable(user)
                .map(u -> isUserInTeam(u, team))
                .orElse(false);

        Integer place = calculateTeamPlace(team);
        Integer points = calculateTeamPoints(team);

        return new TeamListDto(
                team.getId(),
                team.getName(),
                place,
                points,
                userCount,
                membersCount,
                isMyTeam
        );
    }
}


