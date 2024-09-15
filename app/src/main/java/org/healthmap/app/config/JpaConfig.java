package org.healthmap.app.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "org.healthmap.db")
@EnableJpaRepositories(basePackages = "org.healthmap.db")
public class JpaConfig {
}
