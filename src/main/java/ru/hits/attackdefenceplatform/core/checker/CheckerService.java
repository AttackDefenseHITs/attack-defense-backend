package ru.hits.attackdefenceplatform.core.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerService {
    private final String checkersDirectory = "/var/lib/checkers/";

    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final CheckerValidator checkerValidator;

    public void uploadChecker(MultipartFile file, UUID serviceId) throws IOException {
        var service = vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        var originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".py")) {
            throw new IllegalArgumentException("Only .py files are allowed");
        }

        var filePath = checkersDirectory + UUID.randomUUID() + "_" + originalFilename;
        var scriptFile = new File(filePath);
        file.transferTo(scriptFile);

        if (!checkerValidator.validateSyntax(scriptFile) || !checkerValidator.validateFunctions(scriptFile)) {
            throw new IllegalArgumentException("Checker script is invalid");
        }
    }
}
