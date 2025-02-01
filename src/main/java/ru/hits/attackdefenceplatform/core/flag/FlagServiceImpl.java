package ru.hits.attackdefenceplatform.core.flag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.common.exception.flag.FlagExpiredException;
import ru.hits.attackdefenceplatform.common.exception.flag.InvalidFlagException;
import ru.hits.attackdefenceplatform.common.exception.flag.OwnFlagSubmissionException;
import ru.hits.attackdefenceplatform.core.FlagCostProperties;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {

    private final FlagCostProperties flagCostProperties;

    private final FlagRepository flagRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;

    @Override
    @Transactional
    public void sendFlag(String flagValue, UserEntity user) {
        var teamMember = teamMemberRepository.findByUser(user)
                .orElseThrow(() -> new TeamException("Пользователь не является участником соревнований"));

        try {
            var currentFlag = flagRepository.findByValue(flagValue)
                    .orElseThrow(() -> new InvalidFlagException("Неправильное значение флага"));

            if (!currentFlag.getIsActive()) {
                throw new FlagExpiredException("Флаг больше не активен");
            }

            var userTeam = teamMember.getTeam();
            if (currentFlag.getFlagOwner().equals(userTeam)) {
                throw new OwnFlagSubmissionException("Вы не можете отправить флаг своей команды");
            }

            currentFlag.setIsActive(false);

            teamMember.setPoints(teamMember.getPoints() + flagCostProperties.getFlagCost());
            saveFlagSubmission(teamMember, currentFlag, flagValue, true);

            teamMemberRepository.save(teamMember);
            flagRepository.save(currentFlag);

        } catch (Exception e) {
            saveFlagSubmission(teamMember, null, flagValue, false);
            throw e;
        }
    }


    private void saveFlagSubmission(TeamMemberEntity teamMember, FlagEntity flag, String flagValue, boolean isCorrect) {
        var flagSubmission = new FlagSubmissionEntity();
        flagSubmission.setTeamMember(teamMember);
        flagSubmission.setSubmittedFlag(flagValue);
        flagSubmission.setSubmissionTime(new Date());
        flagSubmission.setIsCorrect(isCorrect);
        flagSubmission.setFlag(flag);

        flagSubmissionRepository.save(flagSubmission);
    }
}
