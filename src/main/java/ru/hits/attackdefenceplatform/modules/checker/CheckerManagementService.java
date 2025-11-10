package ru.hits.attackdefenceplatform.modules.checker;

import java.io.IOException;
import java.util.UUID;

public interface CheckerManagementService {
    void uploadChecker(String scriptText, UUID serviceId) throws IOException;
    String getCheckerScript(UUID serviceId) throws IOException;
}
