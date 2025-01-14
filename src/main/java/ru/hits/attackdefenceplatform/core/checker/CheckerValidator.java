package ru.hits.attackdefenceplatform.core.checker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

@Component
@Slf4j
public class CheckerValidator {
    private static final List<String> REQUIRED_FUNCTIONS = List.of("check", "put", "get", "get_flag");

    public boolean validateSyntax(File scriptFile) {
        try {
            var process = new ProcessBuilder("python3", "-m", "py_compile", scriptFile.getAbsolutePath())
                    .redirectErrorStream(true)
                    .start();

            // Чтение вывода процесса
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(log::info);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Syntax validation failed for script {}. Exit code: {}", scriptFile.getAbsolutePath(), exitCode);
            }

            return exitCode == 0;
        } catch (Exception e) {
            log.error("Exception occurred during syntax validation for script {}: {}", scriptFile.getAbsolutePath(), e.getMessage());
            return false;
        }
    }
}

