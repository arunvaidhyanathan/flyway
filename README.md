# flyway
flyway for data base versioning.

## Prerequisites:

Java 17 JDK: Make sure you have Java 17 (or later) installed. You can download it from https://www.oracle.com/java/technologies/javase-jdk17-downloads.html or use a distribution like Adoptium Temurin.
Maven or Gradle: Choose either Maven or Gradle as your build tool. I'll provide instructions for both.
IDE (Optional): An IDE like IntelliJ IDEA, Eclipse, or Visual Studio Code is helpful but not strictly required.
Existing PostgreSQL Database: You have a PostgreSQL database named workflow with a schema cads and existing database objects. You know the connection details (host, port, username, password).
Docker: Have a local postgresql using docker.
Step 1: Project Setup (Maven Example)

## Create a New Maven Project
You can use your IDE or the command line to create a new Maven project. Using the command line:
mvn archetype:generate \
  -DgroupId=com.example \
  -DartifactId=flyway-demo \
  -Dversion=0.0.1-SNAPSHOT \
  -Dname="Flyway Demo" \
  -DarchetypeArtifactId=spring-boot-starter-web-archetype \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeVersion=3.2.0

## This will create a directory named flyway-demo.
Update pom.xml:
Open the pom.xml file and add the following dependencies:
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>flyway-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>flyway-demo</name>
    <description>Flyway Demo Project</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

## Explanation:
spring-boot-starter-web: For creating a simple web application (you might not need this if you just want database migrations).
flyway-core: The Flyway library itself.
postgresql: The PostgreSQL JDBC driver.
spring-boot-starter-jdbc: For database connection management.
Create a Spring Boot Application Class:
Create a Java class (e.g., FlywayDemoApplication.java) in the src/main/java/com/example directory:
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlywayDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlywayDemoApplication.class, args);
    }
}

## Step 2: Configure Database Connection

application.properties or application.yml:
Create an application.properties (or application.yml) file in the src/main/resources directory to configure your database connection:
application.properties:
spring.datasource.url=jdbc:postgresql://localhost:5432/workflow
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.flyway.schemas=cads
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=false

## Explanation:
spring.datasource.url: The JDBC URL for your PostgreSQL database. Replace localhost:5432 with your actual host and port.
spring.datasource.username: Your PostgreSQL username.
spring.datasource.password: Your PostgreSQL password.
spring.flyway.schemas: Sets the schema(s) that Flyway will manage (in your case, cads).
spring.flyway.baseline-on-migrate=true: This tells Flyway to automatically create a baseline migration if one doesn't exist. Very important for existing databases!
spring.flyway.validate-on-migrate=false: To prevent issues with validation, particularly when dealing with existing databases.

If you're using application.yml:
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/workflow
    username: your_user
    password: your_password
  flyway:
    schemas: cads
    baseline-on-migrate: true
    validate-on-migrate: false

Step 3: Baseline the Existing Database

## Run the Application:
Run your Spring Boot application. This will trigger Flyway to connect to the database and perform the baseline migration. In your IDE, you can usually right-click on the FlywayDemoApplication class and select "Run." From the command line:
mvn clean install
mvn spring-boot:run

## Verify the Baseline:
After the application starts, Flyway will have created a baseline migration. Check your database for the flyway_schema_history table in the public schema (this is Flyway's default location for its metadata table). This table will contain a row representing the baseline migration with a version number (e.g., 1) and a description (e.g., << Flyway Baseline >>).
If the flyway_schema_history table is not present, ensure the connection details are correct and the user has permission to create tables.
Step 4: Create a Future Migration

## Create a Migration Script:
Flyway uses a specific naming convention for migration scripts: V<version>__<description>.sql.
V: Indicates a versioned migration.
<version>: A version number (e.g., 1, 2, 3, etc.). This must be higher than the baseline version.
__: Two underscores separating the version and description.
<description>: A descriptive name for the migration (e.g., add_new_column, create_index).
.sql: The file extension.
Create a new SQL file in the src/main/resources/db/migration directory. For example, src/main/resources/db/migration/V2__add_email_to_business_user.sql:
-- src/main/resources/db/migration/V2__add_email_to_business_user.sql

ALTER TABLE abcd.business_user ADD COLUMN email VARCHAR(255);

Explanation: This script adds an email column to the business_user table in the cads schema.
Run the Application Again:
Run your Spring Boot application again. Flyway will detect the new migration script and apply it to your database.
Verify the Migration:
Check the flyway_schema_history table. You should now see a row for your new migration (V2__add_email_to_business_user.sql) indicating that it has been applied.

## Important Considerations

Idempotency: Make sure your migration scripts are idempotent. This means that if you run the same script multiple times, it should have the same effect as running it once. This is especially important for complex migrations.
Transactions: Wrap your migration scripts in transactions to ensure that if any part of the script fails, the entire script is rolled back, preventing data corruption.
Testing: Test your migration scripts thoroughly in a development or staging environment before applying them to production.
Rollbacks: Flyway supports rollbacks to previous versions. However, you need to create separate rollback scripts for each migration. Consider the complexity of rollbacks when designing your migrations.
SQL Style Guide: Follow a consistent SQL style guide to improve readability and maintainability.
Baseline Location: Be aware that spring.flyway.baseline-on-migrate=true also relies on the baseline version being correctly set. Explicitly set a baseline version if needed: spring.flyway.baseline-version=1
Schema Management: Ensure the Flyway user has appropriate permissions on the schema(s) it manages (typically cads in your case).
flyway_schema_history Location: Make sure that the flyway user has permission to create history table. If not give permission for the flyway history table.