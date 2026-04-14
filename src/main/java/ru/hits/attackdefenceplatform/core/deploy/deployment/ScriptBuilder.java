package ru.hits.attackdefenceplatform.core.deploy.deployment;

import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

@Component
public class ScriptBuilder {
    public String buildDeploymentScript(VulnerableServiceEntity service) {
        String serviceName = service.getName();
        String repoUrl = service.getGitRepositoryUrl();

        return String.format("""
                #!/bin/bash
                set -ex

                exec > >(tee -a /opt/deploy.log) 2>&1
                REPO_URL="%s"
                ROOT_DIR="/opt/services"
                SERVICE_NAME="%s"
                SERVICE_DIR="$ROOT_DIR/services/$SERVICE_NAME"

                echo "Deploying service: $SERVICE_NAME"
                echo "Repository: $REPO_URL"

                sudo mkdir -p "$ROOT_DIR"
                cd "$ROOT_DIR"

                if [ ! -d ".git" ]; then
                    echo "Первый деплой — sparse clone..."

                    sudo env GIT_TERMINAL_PROMPT=0 git clone --progress --filter=blob:none --no-checkout "$REPO_URL" .

                    sudo git sparse-checkout init --cone
                    sudo git sparse-checkout set "services/$SERVICE_NAME"
                    sudo git checkout -B main origin/main
                else
                    echo "Обновление сервиса..."

                    sudo env GIT_TERMINAL_PROMPT=0 git fetch origin main --progress
                    sudo git sparse-checkout set "services/$SERVICE_NAME"

                    if ! sudo git rev-parse --verify HEAD >/dev/null 2>&1; then
                        echo "HEAD отсутствует, выполняем первый checkout ветки main..."
                        sudo git checkout -B main origin/main
                    else
                        echo "Сброс локальных изменений..."
                        sudo git reset --hard HEAD
                        sudo git clean -fd
                        sudo git checkout main
                        sudo env GIT_TERMINAL_PROMPT=0 git pull --progress origin main
                    fi
                fi

                echo "Проверяем директорию сервиса: $SERVICE_DIR"

                if [ ! -d "$SERVICE_DIR" ]; then
                    echo "❌ Ошибка: сервис '$SERVICE_NAME' не найден в репозитории!"
                    exit 1
                fi

                sudo chmod -R 777 "$SERVICE_DIR"

                echo "Переход в директорию сервиса"
                cd "$SERVICE_DIR"

                echo "Перезапуск docker compose..."
                sudo docker compose down || true
                sudo docker compose up -d --build

                echo "✔ Деплой сервиса '$SERVICE_NAME' завершён."
                """,
                repoUrl,
                serviceName
        );
    }
}



