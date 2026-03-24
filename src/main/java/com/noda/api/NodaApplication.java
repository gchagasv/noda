package com.noda.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure. jdbc.DataSourceAutoConfiguration.class})
public class NodaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NodaApplication.class, args);
	}

}

