package ru.hits.attackdefenceplatform.modules.checker.script;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerFileServiceNew {

    private static final Path STORAGE_ROOT = Paths.get("/var/lib/checkers");

    /**
     * Распаковка архива во временную директорию
     */
    public Path extractCheckerArchive(MultipartFile archive) throws IOException {
        Path tempDir = Files.createTempDirectory("checker_");

        try (InputStream is = archive.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path outFile = tempDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(outFile);
                } else {
                    Files.createDirectories(outFile.getParent());
                    Files.copy(zis, outFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        return tempDir;
    }

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

    /**
     * Удаляет список файлов чекера
     */
    public void deleteCheckerFiles(List<String> paths) {
        for (String path : paths) {
            try {
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException e) {
                log.warn("Не удалось удалить файл чекера: {}", path, e);
            }
        }
    }

    /**
     * Собирает ZIP-архив из списка файлов
     */
    public byte[] buildArchive(List<String> filePaths) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String pathStr : filePaths) {
                Path path = Paths.get(pathStr);

                ZipEntry entry = new ZipEntry(path.getFileName().toString());
                zos.putNextEntry(entry);

                Files.copy(path, zos);

                zos.closeEntry();
            }
        }

        return baos.toByteArray();
    }


    // ------------------------- Internal helpers ---------------------------

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
