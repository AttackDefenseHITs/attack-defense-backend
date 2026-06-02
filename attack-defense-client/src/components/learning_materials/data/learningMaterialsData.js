export const initialLearningPages = [
    {
        id: "getting-started",
        title: "Как начать",
        visibleToUsers: true,
        content: `
      <p>Это моковая страница учебных материалов.</p>
      <p><strong>ADMIN</strong> может редактировать контент прямо в браузере (в памяти приложения).</p>
      <p><strong>USER</strong> видит только чтение и может переключать страницы слева.</p>
    `,
    },
    {
        id: "basic-rules",
        title: "Базовые правила",
        visibleToUsers: false,
        content: `
      <ol>
        <li>Используйте навигацию в хэдере.</li>
        <li>Выберите страницу.</li>
        <li><strong>ADMIN</strong> — редактирует, <strong>USER</strong> — читает.</li>
      </ol>
      <p>Данные не сохраняются на сервер: при обновлении вкладки всё сбрасывается.</p>
      <p>Пример кода:</p>
      <pre><code>print("hello, materials")</code></pre>
    `,
    },
    {
        id: "api_attack",
        title: "API для атак",
        visibleToUsers: true,
        content: `
      <h4>Общая информация</h4>
      <p>Для автоматизации атак вы можете использовать API платформы.</p>

      <h4>Отслеживание начала раунда</h4>
      <p>
        Начало и события раунда можно отслеживать через <strong>WebSocket</strong>.
        Это позволяет автоматически запускать ваши скрипты в нужный момент.
      </p>

      <ul>
        <li>Подключитесь к WebSocket серверу</li>
        <li>Ожидайте событие начала раунда</li>
        <li>Запускайте атаку</li>
      </ul>

      <h4>Отправка флагов</h4>
      <p>Флаги отправляются через HTTP API с использованием токена авторизации.</p>

      <h6>Endpoint</h6>
      <pre><code>POST /api/flags/send</code></pre>

      <h6>Headers</h6>
      <pre><code>Authorization: Bearer &lt;your_token&gt;
Content-Type: application/json</code></pre>

      <h6>Request Body</h6>
      <pre><code>{
  "flagValue": "string"
}</code></pre>

      <h6>Пример (Python)</h6>
      <pre><code>import requests

url = "https://your-domain.com/api/flags/send"

headers = {
    "Authorization": "Bearer YOUR_TOKEN",
    "Content-Type": "application/json"
}

data = {
    "flagValue": "FLAG{example}"
}

response = requests.post(url, json=data, headers=headers)

print(response.status_code, response.text)</code></pre>

      <h6>Рекомендации</h6>
      <ul>
        <li>Автоматизируйте отправку флагов сразу после нахождения</li>
        <li>Обрабатывайте ошибки API (например, дубликаты)</li>
        <li>Не храните токен в открытом виде</li>
      </ul>
    `,
    },
];