package ru.hits.attackdefenceplatform.business.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionEntity;
import ru.hits.attackdefenceplatform.business.dashboard.repository.FlagSubmissionRepository;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionDto;

@Service
@RequiredArgsConstructor
public class FlagSubmissionServiceImpl implements FlagSubmissionService {
    private final FlagSubmissionRepository flagSubmissionRepository;

    @Override
    public Page<FlagSubmissionDto> getFlagSubmissions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submissionTime"));

        Page<FlagSubmissionEntity> submissionPage = flagSubmissionRepository.findAll(pageable);
        return submissionPage.map(this::createFlagSubmissionDto);
    }

    private FlagSubmissionDto createFlagSubmissionDto(
            FlagSubmissionEntity entity
    ) {
        var serviceName = entity.getIsCorrect() ? entity.getFlag().getVulnerableService().getName() : null;

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
