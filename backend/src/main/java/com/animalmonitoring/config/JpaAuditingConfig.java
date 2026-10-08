package com.animalmonitoring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing so @CreatedDate fields are auto-populated.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
