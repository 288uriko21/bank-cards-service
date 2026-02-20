# Bank Cards REST API

Учебный REST‑сервис мини‑банка на Spring Boot: пользователи, банковские карты и переводы между ними. 

## Технологии

- Java 17, Spring Boot 3  
- Spring Web, Spring Security (JWT)  
- Spring Data JPA (Hibernate) 
- PostgreSQL 
- Liquibase (миграции БД)  
- Swagger / OpenAPI (документация API)  
- Docker / Docker Compose  
- Postman (ручное тестирование API)

## Запуск

1. Клонировать репозиторий:

```bash
   git clone https://github.com/288uriko21/bank-cards-service.git
   cd bank-cards-service
```
2. Поднять PostgreSQL в Docker:

```bash
docker compose up -d postgres
```
3. Запустить приложение:

```bash
mvn spring-boot:run
```

При первом запуске Liquibase создаст таблицы и добавит тестовых пользователей.

Тестовые пользователи:

 - admin / admin — роль ADMIN
 - user / user — роль USER

### Основные эндпоинты
* Аутентификация
    - POST /api/auth/login — логин, получение JWT‑токена
    - PATCH /api/auth/me/password — изменение своего пароля

* Пользователи (ADMIN)
    - GET /api/users — получить всех пользователей
    - POST /api/users — создать пользователя
    - PATCH /api/users/{id}/reset-password — сброс пароля пользователя
    - GET /api/users/{id} — получить пользователя по id
    - DELETE /api/users/{id} — удалить пользователя

* Карты
    - GET /api/cards — все карты (ADMIN)
    - POST /api/cards — создать банковскую карту
    - POST /api/cards/{id}/block-request — запрос на блокировку своей карты
    - PATCH /api/cards/{id}/block — блокировка любой карты (ADMIN)
    - PATCH /api/cards/{id}/activate — активация карты (ADMIN)
    - GET /api/cards/{id}/transfers — транзакции по карте (ADMIN)
    - GET /api/cards/my — карты текущего пользователя
    - DELETE /api/cards/{id} — удалить карту (ADMIN)

* Переводы
    - POST /api/transfers — внутренний перевод между своими картами
    - POST /api/transfers/external — внешний перевод на любую карту
    - GET /api/transfers/my — мои переводы, история

* Прочее
    - GET /ping — проверка доступности сервиса 

Полное описание эндпоинтов доступно после запуска приложения. Swagger UI: http://localhost:8080/swagger-ui/index.html
