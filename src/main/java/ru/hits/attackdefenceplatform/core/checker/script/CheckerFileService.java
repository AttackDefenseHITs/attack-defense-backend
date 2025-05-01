package ru.hits.attackdefenceplatform.core.checker.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Сервис для работы с файлами скриптов чекеров.
 */
@Service
@Slf4j
public class CheckerFileService {
    private final String checkersDirectory;

    /**
     * Конструктор сервиса, инициализирующий директорию для чекеров.
     *
     * @param checkersDirectory путь к директории, где будут храниться чекеры (подставляется из application.properties)
     */
    public CheckerFileService(@Value("${checkers.directory}") String checkersDirectory) {
        this.checkersDirectory = checkersDirectory;
    }

    /**
     * Сохраняет текст скрипта в новый файл в директории чекеров.
     *
     * <p>Метод генерирует уникальное имя файла с суффиксом "_checker.py" и записывает переданный текст в него.</p>
     *
     * @param scriptText текст скрипта, который необходимо сохранить
     * @return путь к созданному файлу скрипта
     * @throws IOException если произошла ошибка ввода-вывода или не удалось создать директорию
     */
    public Path saveScriptToFile(String scriptText) throws IOException {
        ensureCheckersDirectoryExists();
        var fileName = UUID.randomUUID() + "_checker.py";
        var scriptPath = Paths.get(checkersDirectory, fileName);
        Files.writeString(scriptPath, scriptText);
        return scriptPath;
    }

    /**
     * Считывает содержимое файла скрипта по заданному пути.
     *
     * @param scriptFilePath строковое представление пути к файлу скрипта
     * @return содержимое файла скрипта в виде строки
     * @throws IOException если файл не найден или произошла ошибка чтения
     */
    public String readScriptFromFilePath(String scriptFilePath) throws IOException {
        var scriptPath = Paths.get(scriptFilePath);
        return Files.readString(scriptPath);
    }

    /**
     * Удаляет файл скрипта по заданному пути.
     *
     * <p>Если файл существует, производится его удаление. В случае ошибки удаление выбрасывается RuntimeException.</p>
     *
     * @param scriptFilePath строковое представление пути к файлу скрипта
     */
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

    /**
     * Обеспечивает наличие директории для хранения чекеров.
     *
     * <p>Если директория не существует, она будет создана.</p>
     *
     * @throws IOException если не удалось создать директорию
     */
    private void ensureCheckersDirectoryExists() throws IOException {
        Path checkersDirPath = Paths.get(checkersDirectory);
        if (!Files.exists(checkersDirPath)) {
            Files.createDirectories(checkersDirPath);
            log.info("Создана директория для чекеров: {}", checkersDirectory);
        }
    }
}



