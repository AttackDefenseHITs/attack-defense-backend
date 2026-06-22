package ru.hits.attackdefenceplatform.core.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerEntity;
import ru.hits.attackdefenceplatform.core.checker.repository.CheckerRepository;
import ru.hits.attackdefenceplatform.core.checker.script.CheckerFileService;
import ru.hits.attackdefenceplatform.core.repo.RepositoryAdapter;
import ru.hits.attackdefenceplatform.core.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.checker.FileNodeDto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckerManagementServiceImpl implements CheckerManagementService {

    private final CheckerRepository checkerRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;

    private final RepositoryAdapter repositoryAdapter;
    private final CheckerFileService checkerFileService;

    @Override
    @Transactional
    public void syncCheckers(
            String repoName,
            String branch,
            Map<String, List<RepoFileDto>> detected
    ) throws IOException {

        Set<String> processed = new HashSet<>();

        for (var entry : detected.entrySet()) {
            String serviceName = entry.getKey().toLowerCase();
            List<RepoFileDto> files = entry.getValue();

            VulnerableServiceEntity service = vulnerableServiceRepository.findByNameIgnoreCase(serviceName)
                    .orElseThrow(() -> new IllegalStateException(
                            "Для чекера не найден сервис: " + serviceName));

            Path tempDir = Files.createTempDirectory("checker_sync_");

            for (RepoFileDto file : files) {
                byte[] content = repositoryAdapter.getFileContent(repoName, file.getPath(), branch);
                Path relative = Paths.get(file.getPath()).subpath(2, Paths.get(file.getPath()).getNameCount());
                Path out = tempDir.resolve(relative);

                Files.createDirectories(out.getParent());
                Files.write(out, content);
            }

            Path storedRoot = checkerFileService.saveCheckerDirectory(tempDir, serviceName);
            installRequirements(storedRoot);

            CheckerEntity checker = checkerRepository.findByVulnerableServiceId(service.getId())
                    .orElseGet(() -> {
                        CheckerEntity c = new CheckerEntity();
                        c.setVulnerableService(service);
                        return c;
                    });

            checker.setScriptFilePath(storedRoot.toString());

            checkerRepository.save(checker);
            processed.add(serviceName);
        }

        checkerRepository.findAll().stream()
                .filter(c -> !processed.contains(c.getVulnerableService().getName().toLowerCase()))
                .forEach(checkerRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public String getCheckerScript(UUID serviceId) throws IOException {
        var checkerEntity = checkerRepository.findByVulnerableServiceId(serviceId).orElse(null);
        return checkerEntity == null ? "" : checkerFileService.readScriptFromFilePath(checkerEntity.getScriptFilePath() + "/run.py");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileNodeDto> getCheckerFileTree(UUID serviceId) throws IOException {

        CheckerEntity checker = checkerRepository.findByVulnerableServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Чекер не найден"));

        Path root = Paths.get(checker.getScriptFilePath());

        if (!Files.exists(root) || !Files.isDirectory(root)) {
            throw new IllegalStateException("Корневая директория чекера недоступна: " + root);
        }

        return buildTree(root, root);
    }

    @Override
    @Transactional(readOnly = true)
    public String getCheckerFileContent(UUID serviceId, String relativePath) throws IOException {

        CheckerEntity checker = checkerRepository.findByVulnerableServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Чекер не найден"));

        Path root = Paths.get(checker.getScriptFilePath());
        Path file = root.resolve(relativePath).normalize();

        if (!file.startsWith(root)) {
            throw new SecurityException("Выход за пределы директории чекера");
        }

        if (!Files.exists(file) || Files.isDirectory(file)) {
            throw new FileNotFoundException("Файл не найден: " + relativePath);
        }

        return Files.readString(file);
    }

    private List<FileNodeDto> buildTree(Path root, Path current) throws IOException {
        List<FileNodeDto> nodes = new ArrayList<>();

        try (Stream<Path> children = Files.list(current)) {
            for (Path child : children.toList()) {

                String rel = root.relativize(child).toString();

                if (Files.isDirectory(child)) {
                    nodes.add(new FileNodeDto(
                            child.getFileName().toString(),
                            rel,
                            true,
                            buildTree(root, child)
                    ));
                } else {
                    nodes.add(new FileNodeDto(
                            child.getFileName().toString(),
                            rel,
                            false,
                            null
                    ));
                }
            }
        }
        return nodes;
    }

    private void installRequirements(Path rootDir) {
        Path requirements = rootDir.resolve("requirements.txt");

        if (!Files.exists(requirements)) {
            log.info("requirements.txt отсутствует в {}", rootDir);
            return;
        }

        log.info("Устанавливаю зависимости из {}", requirements);

        try {
            ProcessBuilder pb = new ProcessBuilder("pip3", "install", "-r", requirements.toString());
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (var reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            )) {
                reader.lines().forEach(line -> log.info("[pip] {}", line));
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("pip install завершился ошибкой (exit={})", exitCode);
            } else {
                log.info("Зависимости успешно установлены");
            }

        } catch (Exception e) {
            log.error("Ошибка установки зависимостей: {}", e.getMessage(), e);
        }
    }
}

