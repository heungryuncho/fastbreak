package com.portfolio.fastbreak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FastbreakApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastbreakApplication.class, args);
	}

}
