package ru.hits.attackdefenceplatform.rest.controller.checker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.checker.CheckerService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/checkers")
@Tag(name = "Управление чекерами")
@RequiredArgsConstructor
public class CheckerController {

    private final CheckerService checkerService;

    @PostMapping("/{serviceId}/upload")
    @Operation(summary = "Загрузить чекер для уязвимого сервиса")
    public ResponseEntity<String> uploadChecker(
            @PathVariable UUID serviceId,
            @RequestBody String scriptText)
    {
        try {
            checkerService.uploadChecker(scriptText, serviceId);
            return ResponseEntity.ok("Checker uploaded successfully.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Получить код чекера для определенного сервиса")
    public ResponseEntity<String> getCheckerCode(
            @PathVariable UUID serviceId
    ) throws IOException {
        var code = checkerService.getCheckerScriptByServiceId(serviceId);
        return ResponseEntity.ok(code);
    }
}
