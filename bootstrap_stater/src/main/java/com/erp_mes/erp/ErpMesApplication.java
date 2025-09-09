package com.erp_mes.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ErpMesApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ErpMesApplication.class, args);
	}

}
