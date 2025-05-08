package com.example.weterview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WeterviewApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeterviewApplication.class, args);
	}
}
