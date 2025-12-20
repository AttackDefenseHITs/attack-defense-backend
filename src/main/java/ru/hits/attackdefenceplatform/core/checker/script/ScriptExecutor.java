package ru.hits.attackdefenceplatform.core.checker.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.checker.data.ScriptExecutionResult;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Компонент для выполнения скриптов.
 */
@Component
@Slf4j
public class ScriptExecutor {
    private final static String USED_LANGUAGE = "python3";

    /**
     * Выполняет Python-скрипт с заданными параметрами.
     *
     * <p>Метод запускает процесс с использованием заданного интерпретатора (python3) и переданных аргументов,
     * считывает вывод процесса, логирует его построчно и возвращает результат выполнения.</p>
     *
     * @param scriptPath путь к исполняемому скрипту
     * @param commands строка с командами для скрипта
     * @param targetIp IP-адрес целевой машины
     * @param targetPort порт целевой машины
     * @return объект ScriptExecutionResult, содержащий результат выполнения скрипта и вывод процесса
     * @throws IOException если произошла ошибка ввода-вывода при запуске процесса или чтении его вывода
     * @throws InterruptedException если текущий поток прерван во время ожидания завершения процесса
     */
    public ScriptExecutionResult executeScript(
            String scriptPath,
            String commands,
            String targetIp,
            int targetPort
    ) throws IOException, InterruptedException {
        var processBuilder = new ProcessBuilder(
                USED_LANGUAGE,
                scriptPath,
                targetIp,
                String.valueOf(targetPort),
                commands
        );
        processBuilder.redirectErrorStream(true);

        var process = processBuilder.start();

        var outputLines = new ArrayList<String>();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(line -> {
                outputLines.add(line);
                log.info(line);
            });
        }

        int exitCode = process.waitFor();
        var result = CheckerResult.fromCode(exitCode);

        return new ScriptExecutionResult(result, outputLines);
    }
}



