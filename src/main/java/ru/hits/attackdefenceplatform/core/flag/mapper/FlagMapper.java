package ru.hits.attackdefenceplatform.core.flag.mapper;

import ru.hits.attackdefenceplatform.core.flag.repository.FlagEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.vulnerable.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

public class FlagMapper {

    private FlagMapper() {}

    public static FlagEntity fromCreateFlagRequest(
            CreateFlagRequest request,
            TeamEntity team,
            VulnerableServiceEntity service
    ) {
        var flagEntity = new FlagEntity();
        flagEntity.setPoint(request.points());
        flagEntity.setFlagOwner(team);
        flagEntity.setVulnerableService(service);
        flagEntity.setFlagNumber(request.flagNumberInService());
        flagEntity.setIsActive(true);
        return flagEntity;
    }

    public static FlagDto mapToFlagDto(FlagEntity flag) {
        return new FlagDto(
                flag.getId(),
                flag.getPoint(),
                flag.getFlagNumber(),
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
                flag.getFlagNumber(),
                flag.getFlagOwner().getName(),
                flag.getVulnerableService().getName()
        );
    }
}

