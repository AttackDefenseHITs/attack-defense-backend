package ru.hits.attackdefenceplatform.business.dashboard;

import org.springframework.data.domain.Page;
import ru.hits.attackdefenceplatform.public_interface.dashboard.FlagSubmissionDto;

public interface FlagSubmissionService {
    Page<FlagSubmissionDto> getFlagSubmissions(int page, int size);
}
