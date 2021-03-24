package com.developsoft.comtainer.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("com.developsoft.comtainer")
public class Application {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
