package ru.hits.attackdefenceplatform.core.hint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintPurchaseRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HintPenaltyService {

    private final ServiceHintPurchaseRepository purchaseRepo;

    public double getHintsMultiplier(UUID teamId, UUID serviceId) {
        var purchases = purchaseRepo.findAllByTeam_IdAndTemplate_Service_Id(teamId, serviceId);

        if (purchases.isEmpty()) {
            return 1.0;
        }

        return purchases.stream()
                .map(p -> p.getTemplate().getMultiplier())
                .min(Double::compareTo)
                .orElse(1.0);
    }
}
