package ru.hits.attackdefenceplatform.core.hint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseRepository;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintViewDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HintQueryService {

    private final ServiceHintTemplateRepository templateRepo;
    private final ServiceHintPurchaseRepository purchaseRepo;
    private final TeamMemberRepository teamMemberRepo;

    @Transactional(readOnly = true)
    public List<ServiceHintViewDto> getHintsForService(UserEntity user, UUID serviceId) {

        var member = teamMemberRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User is not in a team"));

        var teamId = member.getTeam().getId();

        var purchasedIds = purchaseRepo
                .findAllByTeam_IdAndTemplate_Service_Id(teamId, serviceId)
                .stream()
                .map(p -> p.getTemplate().getId())
                .collect(Collectors.toSet());

        return templateRepo.findAllByService_IdOrderByLevelAsc(serviceId)
                .stream()
                .map(t -> new ServiceHintViewDto(
                        t.getId(),
                        t.getLevel(),
                        t.getText(),
                        t.getMultiplier(),
                        t.isEnabled(),
                        purchasedIds.contains(t.getId())
                ))
                .toList();
    }
}
