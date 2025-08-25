package com.bootstrap.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BootstrapStaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootstrapStaterApplication.class, args);
	}

}
