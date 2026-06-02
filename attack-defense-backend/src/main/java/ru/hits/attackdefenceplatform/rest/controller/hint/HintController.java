package ru.hits.attackdefenceplatform.rest.controller.hint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.hint.HintPurchaseService;
import ru.hits.attackdefenceplatform.core.hint.HintQueryService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.hint.GetAllHintsResponse;
import ru.hits.attackdefenceplatform.public_interface.hint.GetHintsByServiceIdResponse;
import ru.hits.attackdefenceplatform.public_interface.hint.ServiceHintTemplateDto;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hints")
@Tag(name = "Управление подсказками для участника")
public class HintController {

    private final HintPurchaseService purchaseService;
    private final HintQueryService queryService;

    @Operation(summary = "Список подсказок по сервису с признаком purchased=true/false")
    @GetMapping
    public GetAllHintsResponse getHints(
            @AuthenticationPrincipal UserEntity user
    ) {
        return queryService.getHints(user);
    }

    @Operation(summary = "Получить список подсказок для конкретного сервиса")
    @GetMapping("/service/{serviceId}")
    public GetHintsByServiceIdResponse getServiceHints(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable UUID serviceId
    ) {
        return queryService.getByServiceId(user, serviceId);
    }

    @Operation(summary = "Купить подсказку по templateId (id шаблона)")
    @PostMapping("/{templateId}/buy")
    public ServiceHintTemplateDto buyHint(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable UUID templateId
    ) {
        return purchaseService.buyHint(user, templateId);
    }
}
