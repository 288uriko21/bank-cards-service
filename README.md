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

Полное описание эндпоинтов доступно после запуска приложения. Swagger UI: http://localhost:8080/swagger-ui/index.html
