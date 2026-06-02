package ru.hits.attackdefenceplatform.core.hint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.hint.mapper.ServiceHintTemplateMapper;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseEntity;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseRepository;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateEntity;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamMemberRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintTemplateDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HintPurchaseService {

    private final ServiceHintTemplateRepository templateRepo;
    private final ServiceHintPurchaseRepository purchaseRepo;
    private final TeamMemberRepository teamMemberRepo;

    @Transactional
    public ServiceHintTemplateDto buyHint(UserEntity user, UUID templateId) {

        // 1) пользователь должен быть в команде
        var member = teamMemberRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User is not in a team"));

        var team = member.getTeam();

        // 2) шаблон подсказки
        var template = templateRepo.findById(templateId)
                .filter(ServiceHintTemplateEntity::isEnabled)
                .orElseThrow(() -> new IllegalArgumentException("Hint not found or disabled"));

        // 3) нельзя купить повторно
        if (purchaseRepo.existsByTeam_IdAndTemplate_Id(team.getId(), templateId)) {
            throw new IllegalStateException("Hint already purchased");
        }

        // 4) сохраняем покупку
        var purchase = new ServiceHintPurchaseEntity();
        purchase.setTeam(team);
        purchase.setTemplate(template);
        purchaseRepo.save(purchase);

        // 5) возвращаем DTO
        return ServiceHintTemplateMapper.toDto(template);
    }
}