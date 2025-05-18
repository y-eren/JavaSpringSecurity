package com.eazybytes.springsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Springsection1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springsection1Application.class, args);
	}

}
