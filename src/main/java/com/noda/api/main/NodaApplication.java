package com.noda.api.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories; // Add this
import org.springframework.boot.autoconfigure.domain.EntityScan; // Add this

@SpringBootApplication
@EnableJpaRepositories("com.noda.api.repositories") // Forces Spring to find your Repositories
@EntityScan("com.noda.api.models") // Forces Spring to find your @Entity classes
public class NodaApplication {
	public static void main(String[] args) {
		SpringApplication.run(NodaApplication.class, args);
	}
}
