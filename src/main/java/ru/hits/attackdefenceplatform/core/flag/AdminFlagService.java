package ru.hits.attackdefenceplatform.core.flag;

import ru.hits.attackdefenceplatform.public_interface.flag.CreateFlagRequest;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagDto;
import ru.hits.attackdefenceplatform.public_interface.flag.FlagListDto;

import java.util.List;
import java.util.UUID;

public interface AdminFlagService {
    FlagDto createFlag(CreateFlagRequest request);

    List<FlagListDto> getAllFlags();

    FlagDto getFlagById(UUID id);

    void deleteFlag(UUID id);

    void updateFlag(UUID id, CreateFlagRequest request);

    List<FlagListDto> getFlagsByService(UUID serviceId);

    List<FlagListDto> getFlagsByTeam(UUID teamId);
}
