package ru.hits.attackdefenceplatform.core.flag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {
    private final FlagRepository flagRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public void sendFlag(String flagValue, UserEntity user) {
        var currentFlag = flagRepository.findByValue(flagValue)
                .orElseThrow(() -> new IllegalArgumentException("Неправильное значение флага"));

        if (!currentFlag.getIsActive()) {
            throw new IllegalStateException("Флаг больше не активен");
        }

        var teamMember = teamMemberRepository.findByUser(user)
                .orElseThrow(() -> new TeamException("Пользователь не является участником соревнований"));
        var userTeam = teamMember.getTeam();

        if (currentFlag.getFlagOwner().equals(userTeam)) {
            throw new IllegalArgumentException("Вы не можете отправить флаг своей команды");
        }

        //флаг больше не активен
        currentFlag.setIsActive(false);
        //начисляем очки участнику
        teamMember.setPoints(teamMember.getPoints() + currentFlag.getPoint());

        teamMemberRepository.save(teamMember);
        flagRepository.save(currentFlag);
    }
}
