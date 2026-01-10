package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GetAllHintsResponse(
        Map<UUID, List<ServiceHintViewDto>> hints
) { }
