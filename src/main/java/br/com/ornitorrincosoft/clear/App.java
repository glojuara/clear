package br.com.ornitorrincosoft.clear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class App {
	
	public static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		context = SpringApplication.run(App.class, args);
	}
}
