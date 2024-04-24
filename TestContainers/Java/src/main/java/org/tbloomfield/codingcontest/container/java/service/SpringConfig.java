package org.tbloomfield.codingcontest.container.java.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbloomfield.codingcontest.container.java.executor.JavaExecutor;

/**
 * Encapsulated spring IOC configuration to separate spring-annotations from core business pojos
 */
@Configuration
public class SpringConfig {
	
    @Bean
    public JavaExecutor getJavaExecutor() {
    	return new JavaExecutor();
    }
    
}
