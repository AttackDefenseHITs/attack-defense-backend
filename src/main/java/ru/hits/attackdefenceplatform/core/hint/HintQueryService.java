package ru.hits.attackdefenceplatform.core.hint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseRepository;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateEntity;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.hint.GetAllHintsResponse;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintViewDto;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HintQueryService {

    private final ServiceHintTemplateRepository templateRepo;
    private final ServiceHintPurchaseRepository purchaseRepo;
    private final TeamMemberRepository teamMemberRepo;

    @Transactional(readOnly = true)
    public GetAllHintsResponse getHints(UserEntity user) {

        var memberOpt = teamMemberRepo.findByUser(user);
        if (memberOpt.isEmpty()) {
            return new GetAllHintsResponse(Collections.emptyMap());
        }

        var teamId = memberOpt.get().getTeam().getId();

        // 1) Все купленные подсказки команды (по всем сервисам)
        var purchasedIds = purchaseRepo.findAllByTeam_Id(teamId).stream()
                .map(p -> p.getTemplate().getId())
                .collect(Collectors.toSet());

        // 2) Все шаблоны подсказок (по всем сервисам)
        var data =  templateRepo.findAllByOrderByService_IdAscLevelAsc().stream()
                .filter(ServiceHintTemplateEntity::isEnabled)
                .collect(Collectors.groupingBy(
                        t -> t.getService().getId(),
                        Collectors.mapping(t -> {
                            boolean purchased = purchasedIds.contains(t.getId());
                            String text = purchased ? t.getText() : null;

                            return new ServiceHintViewDto(
                                    t.getId(),
                                    t.getLevel(),
                                    text,
                                    t.getMultiplier(),
                                    purchased
                            );
                        }, Collectors.toList())
                ));

        return new GetAllHintsResponse(data);
    }
}
