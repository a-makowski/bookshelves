# BookShelves API

The BookShelves API is a backend java application built using Spring Boot. BookShelves provides a platform for managing books, shelves, reviews, and ratings. Users can create an account, interact with a database of books, organize their reading lists into shelves, and share their thoughts through reviews and ratings.

## Features:

- **Spring Boot**: Built on top of the Spring Boot framework.
- **SQL Database**: Uses a SQL database to store and manage data efficiently.
- **JWT Authentication**: Implements JSON Web Tokens authentication for secure access to API endpoints.
- **Swagger Documentation**: Includes Swagger documentation for easy exploration and integration of API endpoints.

## Continuous Integration:

- **Dependabot**: Uses Dependabot to automatically check for and apply updates to project dependencies, ensuring that application remains up-to-date with the latest security patches and enhancements.
- **GitHub Actions**: Uses GitHub Actions as the Continuous Integration (CI) tool.

## Local deployment:

##### Quick start with h2 in memory database:
- Application starts with h2 database by default. To start application use this command in a root folder:

        mvn clean spring-boot:run

##### Deployment using MySQL Database in a Docker container:
- To start MySQL Database use this command in a root folder:

        docker-compose up

- After successfully starting the container, you need to start the application with the dev-mysql profile. To start it, use this command in a root folder.

        mvn clean spring-boot:run -Dspring-boot.run.profiles=dev-mysql

## Automatic Database Population:
The BookShelves API automatically populates the database with sample data during starting up. This ensures that the database is preloaded with relevant information, allowing users to quickly start testing the functionality of the BookShelves API without needing to manually insert all data.

- To disable the data population process for h2 database, add this command to application-dev-h2.properties file:

      spring.sql.init.mode=never

- To disable the data population process for MySQL database, open application-dev-mysql.properties file and change:

      spring.sql.init.mode=always
 
  to:

      spring.sql.init.mode=never
