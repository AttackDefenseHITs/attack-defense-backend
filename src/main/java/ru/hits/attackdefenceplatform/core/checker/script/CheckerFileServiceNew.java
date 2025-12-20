package ru.hits.attackdefenceplatform.core.checker.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerFileServiceNew {

    private static final Path STORAGE_ROOT = Paths.get("/var/lib/checkers");

    /**
     * Сохранение распакованной директории чекера в /var/lib/checkers/<serviceName>
     */
    public Path saveCheckerDirectory(Path tempDir, String serviceName) throws IOException {

        Path serviceDir = STORAGE_ROOT.resolve(serviceName);

        // Удаляем старые файлы, если директория существует
        if (Files.exists(serviceDir)) {
            deleteDirectoryRecursive(serviceDir);
        }

        Files.createDirectories(serviceDir);

        try (Stream<Path> walk = Files.walk(tempDir)) {
            walk.forEach(src -> {
                try {
                    if (!Files.isDirectory(src)) {
                        Path relative = tempDir.relativize(src);
                        Path dest = serviceDir.resolve(relative);
                        Files.createDirectories(dest.getParent());
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        return serviceDir;
    }

    private void deleteDirectoryRecursive(Path path) throws IOException {
        if (!Files.exists(path)) return;

        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            log.warn("Не удалось удалить {}", p, e);
                        }
                    });
        }
    }
}
