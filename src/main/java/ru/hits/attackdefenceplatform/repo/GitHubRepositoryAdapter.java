package ru.hits.attackdefenceplatform.repo;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitBuilder;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRef;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GHTreeBuilder;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.repo.model.RepositoryInfoDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Slf4j
public class GitHubRepositoryAdapter implements RepositoryAdapter {
    private final GitHubProperties gitHubProperties;
    private final GitHub gitHubClient;
    private final GHOrganization organization;

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

            return new RepositoryInfoDto(repo.getName(), repo.getHtmlUrl().toString());
        } catch (IOException e) {
            log.error("Произошла ошибка создания репозитория: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to create template repository", e);
        }
    }

    private void createRepositoryStructure(GHRepository repo) throws IOException {
        GHRef mainRef = repo.getRef("heads/main");
        String baseTreeSha = repo.getTreeRecursive("main", 1).getSha();

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
