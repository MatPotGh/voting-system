# Voting System

A simple voting system running in Docker (Spring Boot + PostgreSQL + Swagger UI).

## Requirements

- Docker  
- You do not need Java or Maven installed locally – everything runs inside containers.

## Quick start

Run all commands from the project root directory (where `docker-compose.yml` is located).

1. Build and start the whole stack:
   ```bash
   docker compose up --build
   ```
   
2. Access the application:
   - App: http://localhost:8080  
   - Swagger UI: http://localhost:8080/swagger-ui/index.html  
   - OpenAPI JSON: http://localhost:8080/v3/api-docs
