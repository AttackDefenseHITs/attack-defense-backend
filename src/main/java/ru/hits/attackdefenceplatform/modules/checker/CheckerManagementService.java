package ru.hits.attackdefenceplatform.modules.checker;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface CheckerManagementService {
    void uploadChecker(String scriptText, UUID serviceId) throws IOException;
    void uploadChecker(MultipartFile scriptArchive, UUID serviceId) throws IOException;
    String getCheckerScript(UUID serviceId) throws IOException;
}
