package ru.hits.attackdefenceplatform.core.checker;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface CheckerService {
    void uploadChecker(String scriptText, UUID serviceId) throws IOException;
    String getCheckerScriptByServiceId(UUID serviceId) throws IOException;
    void runChecker(UUID serviceId, UUID teamId, List<String> commands);
}
