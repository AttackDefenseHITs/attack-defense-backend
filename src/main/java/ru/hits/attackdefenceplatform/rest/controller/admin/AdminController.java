package ru.hits.attackdefenceplatform.rest.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@Tag(name = "Панель администратора")
@RequiredArgsConstructor
public class AdminController {
}
