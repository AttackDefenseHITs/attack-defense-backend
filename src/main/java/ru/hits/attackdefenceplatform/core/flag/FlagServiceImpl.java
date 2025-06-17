package ru.hits.attackdefenceplatform.core.flag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.CompetitionException;
import ru.hits.attackdefenceplatform.common.exception.TeamException;
import ru.hits.attackdefenceplatform.common.exception.flag.FlagExpiredException;
import ru.hits.attackdefenceplatform.common.exception.flag.InvalidFlagException;
import ru.hits.attackdefenceplatform.common.exception.flag.OwnFlagSubmissionException;
import ru.hits.attackdefenceplatform.core.competition.CompetitionService;
import ru.hits.attackdefenceplatform.core.competition.enums.CompetitionStatus;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.flag.repository.FlagRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;

import java.util.Date;

/**
 * Реализация сервиса для работы с флагами в соревнованиях.
 */
@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {

    private final CompetitionService competitionService;
    private final FlagRepository flagRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final FlagSubmissionRepository flagSubmissionRepository;

    private static final String FLAG_SUCCESS = "Успешно";
    private static final String FLAG_OWN = "Флаг команды";
    private static final String FLAG_NOT_ACTIVE = "Флаг не активен";
    private static final String FLAG_INCORRECT = "Флаг неверный";

    /**
     * Обрабатывает отправку флага участником соревнования.
     *
     * <p>Метод проверяет, что пользователь является участником соревнований, соревнование находится в активном круге и в состоянии IN_PROGRESS.
     * Затем пытается найти флаг по переданному значению, проверяет его активность и принадлежность к чужой команде.
     * При успешной проверке флаг деактивируется, начисляются баллы участнику, и сохраняется сабмит флага с признаком корректной отправки.
     * В случае возникновения исключения сохраняется сабмит флага с признаком некорректной отправки, а затем исключение пробрасывается дальше.</p>
     *
     * @param flagValue значение флага, переданное участником
     * @param user пользователь, пытающийся отправить флаг
     * @throws TeamException если пользователь не является участником соревнований
     * @throws CompetitionException если текущий момент не позволяет сдачу флага
     * @throws InvalidFlagException если флаг не найден или имеет неправильное значение
     * @throws FlagExpiredException если флаг больше не активен
     * @throws OwnFlagSubmissionException если участник пытается отправить флаг своей команды
     */
    @Override
    @Transactional
    public void sendFlag(String flagValue, UserEntity user) {
        var competitionDto = competitionService.getCompetitionDto();
        var teamMember = teamMemberRepository.findByUser(user)
                .orElseThrow(() -> new TeamException("Пользователь не является участником соревнований"));
        var userTeam = teamMember.getTeam();

        if (competitionDto.currentRound() == 0 || competitionDto.status() != CompetitionStatus.IN_PROGRESS) {
            throw new CompetitionException("Флаг сдавать в текущий момент нельзя");
        }

        var currentFlag = flagRepository.findByValue(flagValue)
                .orElseThrow(() -> {
                    saveFlagSubmission(userTeam, user, null, flagValue, false, FLAG_INCORRECT);
                    return new InvalidFlagException("Неправильное значение флага");
                });

        if (!currentFlag.getIsActive()) {
            saveFlagSubmission(userTeam, user, null, flagValue, false, FLAG_NOT_ACTIVE);
            throw new FlagExpiredException("Флаг больше не активен");
        }

        if (currentFlag.getFlagOwner().equals(userTeam)) {
            saveFlagSubmission(userTeam, user, currentFlag, flagValue, false, FLAG_OWN);
            throw new OwnFlagSubmissionException("Вы не можете отправить флаг своей команды");
        }

        currentFlag.setIsActive(false);
        teamMember.setPoints(teamMember.getPoints() + competitionDto.flagSendCost());

        saveFlagSubmission(userTeam, user, currentFlag, flagValue, true, FLAG_SUCCESS);
        teamMemberRepository.save(teamMember);
        flagRepository.save(currentFlag);
    }


    /**
     * Сохраняет информацию о сабмите флага.
     *
     * <p>Метод создает новую сущность FlagSubmissionEntity, заполняет ее данными о команде, пользователе, значении флага,
     * времени отправки, а также признаком корректности отправки и, при наличии, ссылкой на сущность флага.</p>
     *
     * @param team команда участника, отправившая флаг
     * @param user пользователь, отправивший флаг
     * @param flag сущность флага, если он определен; иначе null
     * @param flagValue строковое значение отправленного флага
     * @param isCorrect флаг, указывающий корректна ли отправка флага
     */
    private void saveFlagSubmission(
            TeamEntity team,
            UserEntity user,
            FlagEntity flag,
            String flagValue,
            boolean isCorrect,
            String result
    ) {
        var flagSubmission = new FlagSubmissionEntity();
        flagSubmission.setTeam(team);
        flagSubmission.setUser(user);
        flagSubmission.setSubmittedFlag(flagValue);
        flagSubmission.setSubmissionTime(new Date());
        flagSubmission.setIsCorrect(isCorrect);
        flagSubmission.setFlag(flag);
        flagSubmission.setResult(result);

        flagSubmissionRepository.save(flagSubmission);
    }
}

