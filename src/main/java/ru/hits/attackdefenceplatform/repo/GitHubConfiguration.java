package ru.hits.attackdefenceplatform.repo;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class GitHubConfiguration {
    private final GitHubProperties gitHubProperties;

    @Bean
    public GitHub gitHubClient() throws IOException {
        return new GitHubBuilder()
                .withOAuthToken(gitHubProperties.getToken())
                .build();
    }
}
