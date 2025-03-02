package com.devarun.flyway.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

/**
 * Configuration class for Flyway database migration
 * This ensures proper baseline and migration of database objects
 */
@Configuration
public class FlywayConfig {

    @Value("${spring.flyway.schemas}")
    private String flywaySchemas;

    /**
     * Configure Flyway with the datasource and schema settings
     * @param dataSource the configured datasource
     * @return configured Flyway instance
     */
    @Bean(name = "flyway")
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(flywaySchemas)
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .load();
        
        // Execute baseline to initialize the schema version table
        flyway.baseline();
        
        // Execute migration to apply all pending migrations
        flyway.migrate();
        
        return flyway;
    }
}