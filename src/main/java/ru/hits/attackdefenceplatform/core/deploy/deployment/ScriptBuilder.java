package ru.hits.attackdefenceplatform.core.deploy.deployment;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

@Component
public class ScriptBuilder {

    public String buildDeploymentScript(VulnerableServiceEntity service) {
        return String.format(
                "if [ -d \"/opt/%s\" ]; then\n" +
                        "  echo \"Обновление сервиса '%s'...\";\n" +
                        "  cd /opt/%s && git pull && docker-compose up -d --build;\n" +
                        "else\n" +
                        "  echo \"Деплой нового сервиса '%s'...\";\n" +
                        "  git clone %s /opt/%s && cd /opt/%s && docker-compose up -d --build;\n" +
                        "fi\n",
                service.getName(),  // Проверка на существование папки
                service.getName(),  // Лог обновления
                service.getName(),  // Путь для pull
                service.getName(),  // Лог деплоя нового сервиса
                service.getGitRepositoryUrl(), // URL репозитория
                service.getName(),  // Путь для clone
                service.getName()   // Путь для docker-compose
        );
    }
}

