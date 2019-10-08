package com.eqarus.sentalyze.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan({"com.eqarus.sentalyze.controller"})
public class GroupEqarusSentalyzeSocialMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroupEqarusSentalyzeSocialMainApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		
		return new RestTemplate();
	}
	
	
	
	
}
