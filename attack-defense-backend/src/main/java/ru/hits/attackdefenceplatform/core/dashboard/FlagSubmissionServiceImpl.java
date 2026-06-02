package ru.hits.attackdefenceplatform.core.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.core.dashboard.repository.spec.FlagSubmissionSpecifications;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionDto;

@Service
@RequiredArgsConstructor
public class FlagSubmissionServiceImpl implements FlagSubmissionService {
    private final FlagSubmissionRepository flagSubmissionRepository;

    @Override
    public Page<FlagSubmissionDto> getFlagSubmissions(int page, int size, String search, Boolean isCorrect) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionTime"));
        var spec = FlagSubmissionSpecifications.createAdminTableSpecification(search, isCorrect);

        Page<FlagSubmissionEntity> submissionPage = flagSubmissionRepository.findAll(spec, pageable);
        return submissionPage.map(this::createFlagSubmissionDto);
    }

    private FlagSubmissionDto createFlagSubmissionDto(FlagSubmissionEntity entity) {
        var serviceName = entity.getFlag() != null ? entity.getFlag().getVulnerableService().getName() : null;

        return new FlagSubmissionDto(
                entity.getSubmittedFlag(),
                entity.getUser().getName(),
                entity.getSubmissionTime(),
                entity.getIsCorrect(),
                serviceName,
                entity.getResult()
        );
    }
}
