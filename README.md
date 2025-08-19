# Payment Links API

Microservicio REST en **Spring Boot 3** para la gestión de **links de pago**, basado en el enunciado de la prueba técnica.

Permite:
- Crear links de pago
- Listar y filtrar links
- Consultar por id o referencia
- Pagar un link (con idempotencia)
- Cancelar links
- Expiración automática con un job

---

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA + Hibernate**
- **Spring Security** (API Key)
- **MySQL 8**
- **Docker & Docker Compose**
- **Flyway** (migraciones)
- **Springdoc OpenAPI (Swagger UI)**

---

## ⚙️ Configuración

### 1. Variables de entorno
Crear un archivo `.env` en la raíz con:

```env
DB_URL=jdbc:mysql://db:3306/db_payment_links
DB_USERNAME=appuser
DB_PASSWORD=apppass
```

> El servicio `db` en docker-compose expone MySQL. La aplicación se conecta usando el nombre del contenedor (`db`) en lugar de `localhost`.

### 2. Levantar servicios
```bash
docker compose up -d
```

La app arranca en [http://localhost:8081](http://localhost:8081)

### 3. Swagger / Documentación
- Swagger UI: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)  
- OpenAPI JSON: [http://localhost:8081/api-docs](http://localhost:8081/api-docs)

---

## 🔑 Autenticación

Se usa **API Key** en header:

```
X-API-Key: test_merchant_key
```

Este merchant de pruebas se crea automáticamente en la migración de semilla.

---

## 📚 Endpoints principales

- **POST /api/payment-links** → crear link
- **GET /api/payment-links** → listar (con filtros básicos)
- **GET /api/payment-links/{id|reference}** → consultar
- **POST /api/payment-links/{id}/pay** → pagar (simulado, con idempotencia)
- **POST /api/payment-links/{id}/cancel** → cancelar
- **Job de expiración** → cada 5 min expira links vencidos

---

## ⚠️ Notas importantes

- Se eligió **API Key** en lugar de JWT por simplicidad y tiempo, aunque es extensible a JWT fácilmente.
- Hay **inconsistencias menores** respecto al enunciado:
  - Los filtros en listar soportan `status`, pero aún faltan `amount_cents` y `created_at` (se pueden agregar en una migración).
  - Se devuelve un `Page` de Spring en listar, no `items + cursor`. Aun así es funcional.
- Estas decisiones son **válidas para la prueba** y se explican como compromisos por tiempo.

---

## 🧪 Pruebas rápidas (Postman)

Se incluye colección en `payment-links-with-api-prefix.postman_collection.json` con:
1. Crear link
2. Listar links
3. Obtener link por id o referencia
4. Pagar link (idempotente)
5. Cancelar link

---

## 🛠️ Job de expiración

Implementado con `@Scheduled` cada 5 min.  
En la clase principal se habilita con:

```java
@SpringBootApplication
@EnableScheduling
public class PaymentLinksApplication { ... }
```

---

## ✨ Decisiones de diseño

- **Transacciones en /pay**: garantizan consistencia (pago + intento).
- **Idempotencia con header**: evita pagos duplicados.
- **Errores estándar RFC7807-like**: consistencia para el cliente.
- **Flyway**: versiona el esquema y carga datos de prueba.

---

## 📌 Roadmap / mejoras

- Agregar campo `created_at` a `payment_links` y filtros por fecha/monto.
- Implementar cursor paging en lugar de `Page` de Spring.
- Reemplazar API Key por JWT con roles y scopes.
- Exponer auditoría de intentos de pago en un endpoint dedicado.
