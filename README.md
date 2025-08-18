# Payment Links API

Este proyecto es una API REST desarrollada con **Spring Boot** que permite gestionar **enlaces de pago** de manera segura y escalable.  
La API utiliza **MySQL** como base de datos y autenticación mediante **API Key** por temas de tiempo, pero se puede mejorar con **JWT**.

---

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Spring Security (API Key)**
- **MySQL 8**
- **Docker & Docker Compose**
- **Flyway** (Migraciones de base de datos)
- **Springdoc OpenAPI (Swagger UI)**

---

## ⚙️ Configuración del Proyecto

### 1. Clonar el repositorio
```bash
git clone https://github.com/ejsantiagoh/payment-links.git
cd payment-links-api

### 2. Configurar entorno
- Crea `.env` con variables DB.
- Levanta DB: `docker-compose up -d`
- Corre app: `mvn spring-boot:run`

### 3. Uso
- Usa `X-API-Key: test_merchant_key` en headers.
- Swagger: http://localhost:8081/swagger-ui.html

### Decisiones
- API Key sobre JWT para simplicidad y tiempo.
- Transacciones en /pay para idempotencia y consistencia.
- Job cron cada 5 min para expiración.
