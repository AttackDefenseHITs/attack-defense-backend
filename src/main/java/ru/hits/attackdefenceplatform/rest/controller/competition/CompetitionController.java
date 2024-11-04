package ru.hits.attackdefenceplatform.rest.controller.competition;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Управление соревнованиями")
@RequiredArgsConstructor
public class CompetitionController {
    
}
