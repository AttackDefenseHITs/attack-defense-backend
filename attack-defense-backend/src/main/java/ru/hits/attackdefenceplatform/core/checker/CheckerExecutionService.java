package ru.hits.attackdefenceplatform.core.checker;

import java.util.List;
import java.util.UUID;

public interface CheckerExecutionService {
    void runChecker(UUID serviceId, UUID teamId, List<String> commands);
    void runAllCheckers(List<String> commands);
}
