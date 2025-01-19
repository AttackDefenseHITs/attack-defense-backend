package ru.hits.attackdefenceplatform.core.checker.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class ScriptExecutor {

    /**
     * Выполнение скрипта с переданными командами.
     *
     * @param scriptPath Путь к скрипту.
     * @param commands Строка с командами.
     * @param targetIp IP-адрес целевой машины.
     * @param targetPort Порт целевой машины.
     * @return Результат выполнения скрипта.
     */
    public CheckerResult executeScript(
            String scriptPath,
            String commands,
            String targetIp,
            int targetPort
    ) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder(
                "python3",
                scriptPath,
                commands,
                targetIp,
                String.valueOf(targetPort)
        );
        processBuilder.redirectErrorStream(true);

        var process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(log::info);
        }

        int exitCode = process.waitFor();
        return CheckerResult.fromCode(exitCode);
    }
}

