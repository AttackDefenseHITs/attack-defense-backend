package ru.hits.attackdefenceplatform.core.hint;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.hint.mapper.ServiceHintTemplateMapper;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateEntity;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintTemplateDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HintAdminService {

    private final ServiceHintTemplateRepository templateRepo;
    private final VulnerableServiceRepository serviceRepo;

    @Transactional
    public ServiceHintTemplateDto createHint(UUID serviceId, String text, double multiplier) {
        if (multiplier <= 0 || multiplier > 1.0) {
            throw new IllegalArgumentException("multiplier must be in (0,1]");
        }

        var service = serviceRepo.findById(serviceId).orElseThrow();
        int nextLevel = templateRepo.findMaxLevelByServiceId(serviceId) + 1;

        var h = new ServiceHintTemplateEntity();
        h.setService(service);
        h.setLevel(nextLevel);
        h.setText(text);
        h.setMultiplier(multiplier);
        h.setEnabled(true);

        return ServiceHintTemplateMapper.toDto(templateRepo.save(h));
    }

    @Transactional
    public void setEnabled(UUID hintTemplateId, boolean enabled) {
        var h = templateRepo.findById(hintTemplateId).orElseThrow();
        h.setEnabled(enabled);
        templateRepo.save(h);
    }

    @Transactional
    public void deleteHint(UUID hintTemplateId) {
        var h = templateRepo.findById(hintTemplateId).orElseThrow();
        UUID serviceId = h.getService().getId();

        templateRepo.delete(h);

        // перенумерация уровней (1..N)
        var all = templateRepo.findAllByService_IdOrderByLevelAsc(serviceId);
        int lvl = 1;
        for (var x : all) {
            if (x.getLevel() != lvl) {
                x.setLevel(lvl);
            }
            lvl++;
        }
        templateRepo.saveAll(all);
    }
}
