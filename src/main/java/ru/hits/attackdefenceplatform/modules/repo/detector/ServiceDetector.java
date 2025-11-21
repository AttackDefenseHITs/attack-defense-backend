package ru.hits.attackdefenceplatform.modules.repo.detector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.modules.repo.model.RepoFileDto;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceDetector implements RepositoryItemDetector<Map<String, VulnerableServiceEntity>> {

    @Override
    public Map<String, VulnerableServiceEntity> detect(List<RepoFileDto> files) {
        return files.stream()
                .filter(f -> f.getPath().startsWith("services/"))
                .filter(f -> f.getName().equalsIgnoreCase("docker-compose.yaml")
                        || f.getName().equalsIgnoreCase("docker-compose.yml"))
                .map(f -> {
                    String serviceName = f.getPath().split("/")[1];
                    VulnerableServiceEntity service = new VulnerableServiceEntity();
                    service.setName(serviceName);
                    // git url устанавливается на уровне syncSvc
                    return service;
                })
                .collect(Collectors.toMap(VulnerableServiceEntity::getName, s -> s, (a, b) -> a));
    }
}
