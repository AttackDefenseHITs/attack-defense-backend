package ru.hits.attackdefenceplatform.core.team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamNotFoundException;
import ru.hits.attackdefenceplatform.common.exception.UserException;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.public_interface.team.CreateManyTeamsRequest;
import ru.hits.attackdefenceplatform.public_interface.team.CreateTeamRequest;
import ru.hits.attackdefenceplatform.public_interface.team.TeamInfoDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.core.user.mapper.UserMapper.mapUserEntityToDto;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public UUID createTeam(CreateTeamRequest request){
        var team = new TeamEntity();
        team.setName(request.name());
        team.setMaxMembers(request.maxMembers());
        var newTeam = teamRepository.save(team);

        return newTeam.getId();
    }

    @Transactional
    public void deleteTeam(UUID id){
        teamRepository.deleteById(id);
    }

    @Transactional
    public void joinToTeam(UserEntity user, UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        boolean isUserInAnyTeam = teamMemberRepository.existsByUser(user);
        if (isUserInAnyTeam) {
            throw new RuntimeException("Пользователь уже состоит в другой команде");
        }

        long currentMembersCount = teamMemberRepository.countByTeam(team);
        if (currentMembersCount >= team.getMaxMembers()) {
            throw new RuntimeException("В команде с ID " + teamId + " нет места для нового участника");
        }

        var teamMember = new TeamMemberEntity();
        teamMember.setUser(user);
        teamMember.setTeam(team);

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void leftFromTeam(UserEntity user, UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var teamMember = teamMemberRepository.findByUserAndTeam(user, team)
                .orElseThrow(() -> new UserException("Пользователь не состоит в команде с ID " + teamId));

        teamMemberRepository.delete(teamMember);
    }

    @Transactional(readOnly = true)
    public TeamInfoDto getTeamById(UUID teamId) {
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Команда с ID " + teamId + " не найдена"));

        var userCount = teamMemberRepository.countByTeam(team);
        var membersCount = team.getMaxMembers();

        var memberList = teamMemberRepository.findByTeam(team).stream()
                .map(member -> mapUserEntityToDto(member.getUser()))
                .toList();

        return new TeamInfoDto(team.getId(), team.getName(), userCount, membersCount, memberList);
    }

    @Transactional(readOnly = true)
    public List<TeamListDto> getAllTeams() {
        return teamRepository.findAll().stream().map(team -> {
            var userCount = teamMemberRepository.countByTeam(team);
            var membersCount = team.getMaxMembers();

            return new TeamListDto(team.getId(), team.getName(), userCount, membersCount);
        }).toList();
    }

    @Transactional
    public List<UUID> createManyTeams(CreateManyTeamsRequest request) {
        List<UUID> teamIds = new ArrayList<>();
        for (long i = 1; i <= request.teamsCount(); i++) {
            String teamName = "Команда " + i;

            var team = new TeamEntity();
            team.setName(teamName);
            team.setMaxMembers(request.maxMembers());

            var newTeam = teamRepository.save(team);
            teamIds.add(newTeam.getId());
        }
        return teamIds;
    }

    @Transactional
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
}
