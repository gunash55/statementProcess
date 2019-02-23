package com.nthr.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class BankStatementProcessingApp {
	
	public static void main(String[] args) {
		SpringApplication.run(BankStatementProcessingApp.class, args);
	}
	
	
}
