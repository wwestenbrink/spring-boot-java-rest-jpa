# Spring boot strap project 
To help boot strap a spring boot project for developing a rest API that persists to a relational database.

- Rest API (Swagger)
- Lombok
- Gradle
- H2 / Postgres database
- DB migrations (through liquibase)
- jmx exports & management endpoints
- Docker / Compose setup

This is always a work in progress.

## API
`http://localhost:8080/api/swagger/`

## Spring Profiles
Profile                       | Effect
------------------------------|-------------
`debug`                       | Enable debug logs
`use-h2-db`                   | Use internal h2 database instead of external postgres. 

## Licence
MIT