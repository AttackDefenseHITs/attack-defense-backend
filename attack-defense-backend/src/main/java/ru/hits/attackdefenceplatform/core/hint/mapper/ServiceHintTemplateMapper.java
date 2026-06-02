package ru.hits.attackdefenceplatform.core.hint.mapper;

import lombok.experimental.UtilityClass;
import ru.hits.attackdefenceplatform.core.hint.repository.ServiceHintTemplateEntity;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintTemplateDto;

@UtilityClass
public class ServiceHintTemplateMapper {

    public static ServiceHintTemplateDto toDto(ServiceHintTemplateEntity e) {
        return new ServiceHintTemplateDto(
                e.getId(),
                e.getService().getId(),
                e.getLevel(),
                e.getText(),
                e.getMultiplier(),
                e.isEnabled()
        );
    }
}
