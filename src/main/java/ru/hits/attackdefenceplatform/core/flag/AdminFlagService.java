package ru.hits.attackdefenceplatform.core.flag;

import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;

import java.util.List;
import java.util.UUID;

public interface AdminFlagService {
    void createFlags(CreateFlagRequest request);

    List<FlagDto> getAllFlags();

    FlagDto getFlagById(UUID id);

    void deleteFlag(UUID id);

    FlagDto changeFlagStatus(UUID id);

    List<FlagDto> getFlagsByService(UUID serviceId);

    List<FlagDto> getFlagsByTeam(UUID teamId);

    void disableAllFlagsForTeam(UUID serviceId, UUID teamId);
}
