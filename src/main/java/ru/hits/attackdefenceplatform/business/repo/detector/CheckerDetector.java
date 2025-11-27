package ru.hits.attackdefenceplatform.business.repo.detector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.business.repo.model.RepoFileDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CheckerDetector implements RepositoryItemDetector<Map<String, List<RepoFileDto>>> {

    @Override
    public Map<String, List<RepoFileDto>> detect(List<RepoFileDto> files) {

        Map<String, List<RepoFileDto>> grouped = files.stream()
                .filter(f -> f.getPath().startsWith("checkers/"))
                .filter(f -> f.getType().equalsIgnoreCase("blob"))
                .collect(Collectors.groupingBy(f -> f.getPath().split("/")[1]));

        Map<String, List<RepoFileDto>> result = new HashMap<>();

        for (var entry : grouped.entrySet()) {
            if (entry.getValue().stream()
                    .noneMatch(f -> f.getPath().endsWith("/run.py"))) {
                continue;
            }
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}

