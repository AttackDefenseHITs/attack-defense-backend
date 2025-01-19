package ru.hits.attackdefenceplatform.core.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

@Component
@Slf4j
public class CheckerValidator {
    private static final List<String> REQUIRED_FUNCTIONS = List.of("check", "put", "get", "get_flag");

    /**
     * Общий метод для валидации скрипта.
     * @param scriptFile Файл скрипта
     * @return true, если скрипт проходит все проверки; иначе false
     */
    public boolean validate(File scriptFile) {
        if (!validateSyntax(scriptFile)) {
            log.error("Script {} failed syntax validation.", scriptFile.getAbsolutePath());
            return false;
        }

        if (!validateRequiredFunctions(scriptFile)) {
            log.error("Script {} failed function validation.", scriptFile.getAbsolutePath());
            return false;
        }

        return true;
    }

    /**
     * Проверяет синтаксическую корректность скрипта с помощью py_compile.
     * @param scriptFile Файл скрипта
     * @return true, если синтаксис корректен; иначе false
     */
    public boolean validateSyntax(File scriptFile) {
        try {
            var process = new ProcessBuilder("python3", "-m", "py_compile", scriptFile.getAbsolutePath())
                    .redirectErrorStream(true)
                    .start();

            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(log::info);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Syntax validation failed for script {}. Exit code: {}", scriptFile.getAbsolutePath(), exitCode);
                return false;
            }

            log.info("Syntax validation passed for script {}", scriptFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            log.error("Exception during syntax validation for script {}: {}", scriptFile.getAbsolutePath(), e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет наличие всех обязательных функций в скрипте.
     * @param scriptFile Файл скрипта
     * @return true, если все обязательные функции присутствуют; иначе false
     */
    public boolean validateRequiredFunctions(File scriptFile) {
        try {
            var lines = Files.readAllLines(scriptFile.toPath());
            var foundFunctions = REQUIRED_FUNCTIONS.stream()
                    .filter(function -> lines.stream().anyMatch(line -> line.matches("^\\s*def\\s+" + function + "\\s*\\(.*\\):.*$")))
                    .toList();

            foundFunctions.forEach(function -> log.info("Function '{}' found in script {}", function, scriptFile.getAbsolutePath()));

            if (foundFunctions.size() == REQUIRED_FUNCTIONS.size()) {
                log.info("All required functions are present in script {}", scriptFile.getAbsolutePath());
                return true;
            } else {
                log.error("Missing required functions in script {}. Found: {}, Required: {}",
                        scriptFile.getAbsolutePath(), foundFunctions, REQUIRED_FUNCTIONS);
                return false;
            }
        } catch (IOException e) {
            log.error("Error reading script file {}: {}", scriptFile.getAbsolutePath(), e.getMessage());
            return false;
        }
    }
}



