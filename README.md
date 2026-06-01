# Task Tracker

Проект представляет собой фуллстек-приложение для управления задачами, реализованное на Java 21 и Spring Boot 4.

## Стек технологий

- **Backend**: Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA.
- **Database**: PostgreSQL 16.
- **Frontend**: HTML5, Bootstrap 5, нативный JavaScript.
- **Инфраструктура**: Docker, Mailpit.
- **Тестирование**: JUnit 5, Mockito.
- **Миграции**: Liquibase.

## Как развернуть и запустить проект

**Требования:**
- Установленный **Docker Desktop**.

**Запуск:**
1. Распакуйте архив.
2. Откройте терминал в корневой папке проекта.
3. Выполните команду:
   ```bash
   docker compose up --build