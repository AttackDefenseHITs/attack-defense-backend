package ru.hits.attackdefenceplatform.modules.repo.github;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitBuilder;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GHTreeEntry;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.modules.repo.RepositoryAdapter;
import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.modules.repo.model.RepositoryInfoDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GitHubRepositoryAdapter implements RepositoryAdapter {
    private final GitHubProperties gitHubProperties;
    private final GitHub gitHubClient;
    private final GHOrganization organization;

    private static final String DEFAULT_BRANCH = "main";
    private static final Map<String, String> DEFAULT_TEMPLATE = Map.ofEntries(
            Map.entry("services/web/docker-compose.yaml",
                    "version: '3'\nservices:\n  web:\n    image: nginx:latest"),
            Map.entry("checkers/web/main.py",
                    "# Example checker\nprint('Checker started')"),
            Map.entry("checkers/web/requirements.txt", "requests\npyyaml"),
            Map.entry("exploits/web/main.py",
                    "# Example exploit\nprint('Exploit started')"),
            Map.entry("results/.gitkeep", "")
    );

    public GitHubRepositoryAdapter(
            GitHubProperties gitHubProperties,
            GitHub gitHubClient
    ) throws IOException {
        this.gitHubProperties = gitHubProperties;
        this.gitHubClient = gitHubClient;
        this.organization = gitHubClient.getOrganization(gitHubProperties.getOrganization());
    }

    /**
     * Создает новый репозиторий в организации и инициализирует его шаблонной структурой.
     */
    @Override
    public RepositoryInfoDto createRepositoryWithTemplate() {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            String repoName = "ad-competition-" + timestamp;

            GHRepository repo = organization.createRepository(repoName)
                    .private_(true)
                    .autoInit(true)
                    .description("Competition repository initialized by platform")
                    .create();

            createRepositoryStructure(repo);

            String branch = repo.getDefaultBranch();
            String lastCommitSha = repo.getBranches().get(branch).getSHA1();

            return new RepositoryInfoDto(
                    repo.getName(),
                    repo.getFullName(),
                    repo.getHtmlUrl().toString(),
                    branch,
                    repo.isPrivate(),
                    lastCommitSha,
                    repo.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    "GITHUB"
            );
        } catch (IOException e) {
            log.error("Ошибка создания репозитория: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Ошибка создания репозитория", e);
        }
    }

    /**
     * Получает информацию о существующем репозитории в организации.
     */
    @Override
    public RepositoryInfoDto getRepositoryInfo(String repositoryName) {
        try {
            // Можно передавать как просто имя ("ad-competition-...") или как "org/name"
            GHRepository repo;
            if (repositoryName.contains("/")) {
                repo = gitHubClient.getRepository(repositoryName);
            } else {
                repo = organization.getRepository(repositoryName);
            }

            if (repo == null) {
                throw new IllegalArgumentException("Репозиторий не найден: " + repositoryName);
            }

            String branch = repo.getDefaultBranch();
            String lastCommitSha = repo.getBranches().get(branch).getSHA1();

            return new RepositoryInfoDto(
                    repo.getName(),
                    repo.getFullName(),
                    repo.getHtmlUrl().toString(),
                    branch,
                    repo.isPrivate(),
                    lastCommitSha,
                    repo.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    "GITHUB"
            );
        } catch (IOException e) {
            log.error("Ошибка при получении информации о репозитории {}: {}", repositoryName, e.getMessage(), e);
            throw new IllegalStateException("Ошибка при получении информации о репозитории", e);
        }
    }

    @Override
    public List<RepoFileDto> getRepositoryTree(String fullRepoName, String branch) {
        try {
            GHRepository repo;

            // Можно передавать либо "org/repo", либо просто "repo"
            if (fullRepoName.contains("/")) {
                repo = gitHubClient.getRepository(fullRepoName);
            } else {
                repo = organization.getRepository(fullRepoName);
            }

            if (repo == null) {
                throw new IllegalArgumentException("Репозиторий не найден: " + fullRepoName);
            }

            // Получаем рекурсивное дерево файлов
            GHTree tree = repo.getTreeRecursive(branch, 1);
            List<GHTreeEntry> entries = new ArrayList<>(tree.getTree());

            // GitHub API ограничивает глубину дерева (1) — поэтому нужно рекурсивно пройтись
            // (Hub4j сам подгрузит недостающие уровни, если вызвать getTreeRecursive на поддиректорию)
            if (tree.isTruncated()) {
                log.warn("GitHub вернул усечённое дерево (truncated=true), данные могут быть неполными");
            }

            // Преобразуем дерево в список RepoFileDto
            return entries.stream()
                    .map(e -> RepoFileDto.builder()
                            .path(e.getPath())
                            .name(Paths.get(e.getPath()).getFileName().toString())
                            .type(e.getType().toLowerCase())
                            .size(e.getSize())
                            .sha(e.getSha())
                            .build())
                    .toList();

        } catch (IOException e) {
            log.error("Ошибка при загрузке дерева репозитория {}: {}", fullRepoName, e.getMessage(), e);
            throw new IllegalStateException("Ошибка при загрузке дерева репозитория", e);
        }
    }

    @Override
    public byte[] getFileContent(String repoName, String filePath, String branch) {
        try {
            GHRepository repo = gitHubClient.getRepository(repoName);
            GHContent content = repo.getFileContent(filePath, branch);

            if (!content.isFile()) {
                throw new IllegalStateException("Путь " + filePath + " является директорией, а не файлом");
            }

            try (InputStream is = content.read()) {
                return is.readAllBytes();
            }

        } catch (IOException e) {
            log.error("Ошибка загрузки файла {} из {}: {}", filePath, repoName, e.getMessage());
            throw new IllegalStateException("Не удалось загрузить файл " + filePath, e);
        }
    }

    private void createRepositoryStructure(GHRepository repo) throws IOException {
        GHRef mainRef = repo.getRef("heads/" + DEFAULT_BRANCH);
        String baseTreeSha = repo.getTreeRecursive(DEFAULT_BRANCH, 1).getSha();

        GHTreeBuilder treeBuilder = repo.createTree().baseTree(baseTreeSha);
        for (var entry : GitHubRepositoryAdapter.DEFAULT_TEMPLATE.entrySet()) {
            treeBuilder.add(entry.getKey(), entry.getValue(), false);
        }

        GHTree tree = treeBuilder.create();

        GHCommitBuilder commitBuilder = repo.createCommit()
                .message("Initialize repository structure")
                .tree(tree.getSha())
                .parent(mainRef.getObject().getSha());
        GHCommit commit = commitBuilder.create();

        mainRef.updateTo(commit.getSHA1());
    }
}
