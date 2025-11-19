package com.insurance.ktmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KtmpApplication {

	public static void main(String[] args) {
		SpringApplication.run(KtmpApplication.class, args);
	}

}
