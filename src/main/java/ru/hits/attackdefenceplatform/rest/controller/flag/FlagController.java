package ru.hits.attackdefenceplatform.rest.controller.flag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.flag.FlagService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.flag.SendFlagRequest;

@RestController
@RequestMapping("/api/flags")
@Tag(name = "Управление флагами для участника")
@RequiredArgsConstructor
public class FlagController {
    private final FlagService flagService;

    @PostMapping("/send")
    @Operation(summary = "Отправить флаг")
    public ResponseEntity<String> sendFlag(@RequestBody SendFlagRequest request, @AuthenticationPrincipal UserEntity user){
        flagService.sendFlag(request.flagValue(), user);
        return ResponseEntity.ok("Супер гуд");
    }
}
