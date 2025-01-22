package ru.hits.attackdefenceplatform.core.flag.mapper;

import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

public class FlagMapper {

    private FlagMapper() {}

    public static FlagEntity fromCreateFlagRequest(
            String value,
            TeamEntity team,
            VulnerableServiceEntity service
    ) {
        var flagEntity = new FlagEntity();
        flagEntity.setValue(value);
        flagEntity.setFlagOwner(team);
        flagEntity.setVulnerableService(service);
        flagEntity.setIsActive(true);
        return flagEntity;
    }

    public static FlagDto mapToFlagDto(FlagEntity flag) {
        return new FlagDto(
                flag.getId(),
                flag.getFlagOwner().getId(),
                flag.getFlagOwner().getName(),
                flag.getVulnerableService().getId(),
                flag.getVulnerableService().getName(),
                flag.getIsActive()
        );
    }

    public static FlagListDto mapToFlagListDto(FlagEntity flag) {
        return new FlagListDto(
                flag.getId(),
                flag.getFlagOwner().getName(),
                flag.getVulnerableService().getName()
        );
    }
}

