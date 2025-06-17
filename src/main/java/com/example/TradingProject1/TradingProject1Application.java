package com.example.TradingProject1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TradingProject1Application {

	public static void main(String[] args) {
		SpringApplication.run(TradingProject1Application.class, args);
	}

}
