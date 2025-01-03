package ru.hits.attackdefenceplatform.core.deploy.status;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.deploy.repository.DeploymentStatusRepository;
import ru.hits.attackdefenceplatform.core.deploy.status.mapper.DeploymentStatusInitializer;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeploymentStatusService {
    private final DeploymentStatusInitializer deploymentStatusInitializer;
    private final DeploymentStatusRepository deploymentStatusRepository;

    @PostConstruct
    public void initDeploymentStatuses() {
        deploymentStatusInitializer.initializeStatusesForAllCombinations();
    }


}
