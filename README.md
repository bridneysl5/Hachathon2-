# 🍪 Oreo Insight Factory - Backend API

Proyecto backend desarrollado para transformar datos de ventas en insights accionables mediante Spring Boot, Spring Security (JWT), eventos asíncronos y análisis con LLM (GitHub Models).

## 👥 Integrantes del Equipo
* **Leyrin Bridneys Aguilar Jorge** (Setup, JWT & Spring Security)
* **Adrian** (CRUD de Ventas, SalesAggregationService & Tests)
* **Yadir** (Procesamiento Asíncrono, GitHub Models & Email Service)

## 🚀 Instrucciones de Ejecución
1. Clonar el repositorio.
2. Configurar las variables de entorno si se requiere conexión real:
    * `GITHUB_TOKEN`
    * `GITHUB_MODELS_URL`
    * `MODEL_ID`
    * `JWT_SECRET`
    * `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
3. Ejecutar la aplicación:
   ```bash
   ./mvnw spring-boot:run