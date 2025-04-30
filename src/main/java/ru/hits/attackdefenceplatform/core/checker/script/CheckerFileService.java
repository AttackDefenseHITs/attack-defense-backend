package ru.hits.attackdefenceplatform.core.checker.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class CheckerFileService {
    private final String checkersDirectory;

    public CheckerFileService(@Value("${checkers.directory}") String checkersDirectory) {
        this.checkersDirectory = checkersDirectory;
    }

    public Path saveScriptToFile(String scriptText) throws IOException {
        ensureCheckersDirectoryExists();
        var fileName = UUID.randomUUID() + "_checker.py";
        var scriptPath = Paths.get(checkersDirectory, fileName);
        Files.writeString(scriptPath, scriptText);
        return scriptPath;
    }

    public String readScriptFromFilePath(String scriptFilePath) throws IOException {
        var scriptPath = Paths.get(scriptFilePath);
        return Files.readString(scriptPath);
    }

    public void deleteScriptFile(String scriptFilePath) {
        Path scriptPath = Paths.get(scriptFilePath);
        if (Files.exists(scriptPath)) {
            try {
                Files.delete(scriptPath);
                log.info("Чекер удален успешно: {}", scriptPath);
            } catch (IOException e) {
                log.error("Ошибка удаления чекера: {}", scriptPath, e);
                throw new RuntimeException("Ошибка удаления чекера: " + scriptPath, e);
            }
        } else {
            log.warn("Файл чекера не найден: {}", scriptPath);
        }
    }

    private void ensureCheckersDirectoryExists() throws IOException {
        Path checkersDirPath = Paths.get(checkersDirectory);
        if (!Files.exists(checkersDirPath)) {
            Files.createDirectories(checkersDirPath);
            log.info("Создана директория для чекеров: {}", checkersDirectory);
        }
    }
}


