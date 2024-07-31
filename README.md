# Forex-Rates-Microservice
Created REST micro-service using Spring Boot that communicates with an external API to  fetch foreign exchange rates for USD from European Central Bank, store this data in a database,  and provide endpoints to access this data.

This Spring Boot microservice fetches and stores foreign exchange rates from the Frankfurter API. It provides endpoints to access stored data and only calls the external API when necessary. The service uses PostgreSQL for data storage and Flyway for database migrations.

Features
Fetches and stores exchange rates from USD to other currencies.
Provides REST endpoints for accessing exchange rates.
Calls the Frankfurter API only when data is unavailable in the database.
Uses Flyway for managing database migrations.
Endpoints
GET /fx: Returns FX rates from USD to EUR, GBP, JPY, CZK. You can specify a target currency.
GET /fx/{targetCurrency}: Returns the 3 latest FX rates from USD to the specified target currency.
Technologies Used
Spring Boot
Spring Data JPA
PostgreSQL
Flyway
Lombok (optional)
JUnit
Prerequisites
Java 11 or higher
PostgreSQL
Maven or Gradle
Setup Instructions
Clone the repository:



git clone https://github.com/yourusername/forex-rates-microservice.git
cd forex-rates-microservice
Configure the database connection in src/main/resources/application.properties:

properties

spring.datasource.url=jdbc:postgresql://localhost:5432/forexdb
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true
Run the application:



mvn spring-boot:run

Fetch FX Rates:



curl -X GET "http://localhost:8080/fx?targetCurrency=EUR" -H "accept: application/json"
Fetch Latest FX Rates for a Specific Currency:



curl -X GET "http://localhost:8080/fx/USD" -H "accept: application/json"
Database Migration
Flyway is used for managing database migrations. Migration scripts are located in src/main/resources/db/migration.

To apply migrations:

mvn flyway:migrate
Testing
Run the tests with:


mvn test
Contributing
Fork the repository and contribute by submitting a pull request. Suggestions and improvements are welcome!
