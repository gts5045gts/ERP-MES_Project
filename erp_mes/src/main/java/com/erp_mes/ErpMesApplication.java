package com.erp_mes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
  "com.erp_mes.erp", "com.erp_mes.mes"
})
@EntityScan(basePackages = {
  "com.erp_mes.erp", "com.erp_mes.mes"
})
public class ErpMesApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ErpMesApplication.class, args);
	}

}
