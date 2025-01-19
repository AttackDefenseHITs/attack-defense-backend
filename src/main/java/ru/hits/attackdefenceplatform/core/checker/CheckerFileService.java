package ru.hits.attackdefenceplatform.core.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class CheckerFileService {
    private final String checkersDirectory = "/var/lib/checkers/";

    public Path saveScriptToFile(String scriptText) throws IOException {
        ensureCheckersDirectoryExists();
        var fileName = UUID.randomUUID() + "_checker.py";
        var scriptPath = Paths.get(checkersDirectory, fileName);
        Files.writeString(scriptPath, scriptText);
        return scriptPath;
    }

    public String readScriptFromFile(String scriptFilePath) throws IOException {
        var scriptPath = Paths.get(scriptFilePath);
        return Files.readString(scriptPath);
    }

    private void ensureCheckersDirectoryExists() throws IOException {
        Path checkersDirPath = Paths.get(checkersDirectory);
        if (!Files.exists(checkersDirPath)) {
            Files.createDirectories(checkersDirPath);
            log.info("Created directory for checkers: {}", checkersDirectory);
        }
    }
}

