package ru.hits.attackdefenceplatform.app.controller.repo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.business.repo.RepositoryService;
import ru.hits.attackdefenceplatform.business.repo.RepositorySyncService;
import ru.hits.attackdefenceplatform.business.repo.model.RepositoryInfoDto;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/repo")
@Tag(name = "Управление репозиториями")
@RequiredArgsConstructor
public class RepositoryController {
    private final RepositoryService repositoryService;
    private final RepositorySyncService repositorySyncService;

    @PostMapping("/create")
    @Operation(summary = "Создать шаблонный репозиторий")
    public ResponseEntity<RepositoryInfoDto> createRepo() {
        var dto = repositoryService.createRepositoryWithTemplate();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/sync")
    @Operation(summary = "Выполнить синхронизацию")
    public ResponseEntity<Void> syncRepo() throws IOException {
        repositorySyncService.syncRepo();
        return ResponseEntity.ok().build();
    }
}
