# Курсовой проект «Сервис перевода денег»

## Описание
REST-сервис для перевода денег с карты на карту.

Реализованы методы:
- `POST /transfer`
- `POST /confirmOperation`

Формат ответов:
- успех: `{ "operationId": "..." }`
- ошибка: `{ "message": "...", "id": ... }`

## Схема приложений
```text
FRONT (браузер)
   |
   | HTTP/JSON
   v
Money Transfer API (Spring Boot, localhost:5500)
   |                    \
   |                     \
   v                      v
In-memory storage       transfers.log
(cards + operations)    (лог переводов)
```

## Архитектура и хранение данных
- `controller` — REST-эндпоинты.
- `service` — бизнес-логика перевода и подтверждения.
- `repository` — хранение данных в памяти.
- `exception` — обработка ошибок `400/500`.
- `dto/model` — модели запросов/ответов и доменные объекты.

### Хранение карт
Данные карт хранятся в памяти (`InMemoryCardRepository`).
Предзагружены тестовые карты:
- `1111222233334444` (`12/29`, `123`)
- `5555666677778888` (`11/28`, `456`)

## Лог переводов
Все операции пишутся в файл `logs/transfers.log`.
Для каждой операции фиксируются:
- дата и время,
- карта списания,
- карта зачисления,
- сумма,
- комиссия,
- результат операции.

## Запуск
Порт приложения: `5500`.

### Локальный запуск
```bash
./gradlew bootRun
```

### Запуск в Docker
```bash
docker build -t money-transfer .
docker run -p 5500:5500 money-transfer
```

### Запуск через Docker Compose
```bash
docker compose up --build
```

## Проверка через curl
### 1. Создать перевод
```bash
curl -X POST http://localhost:5500/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "cardFromNumber": "1111222233334444",
    "cardFromValidTill": "12/29",
    "cardFromCVV": "123",
    "cardToNumber": "5555666677778888",
    "amount": {
      "value": 10000,
      "currency": "RUR"
    }
  }'
```

### 2. Подтвердить перевод
```bash
curl -X POST http://localhost:5500/confirmOperation \
  -H "Content-Type: application/json" \
  -d '{
    "operationId": "<operationId>",
    "code": "0000"
  }'
```

## Тесты
```bash
./gradlew test
```

Интеграционный тест с Testcontainers (при установленном Docker):
```bash
./gradlew test -DrunDockerIT=true --tests "*MoneyTransferContainerIT"
```

## Проверка с FRONT
- локальный FRONT: `http://localhost:3000`
- демо FRONT: `https://serp-ya.github.io/card-transfer/`
- backend должен быть запущен на `http://localhost:5500`
