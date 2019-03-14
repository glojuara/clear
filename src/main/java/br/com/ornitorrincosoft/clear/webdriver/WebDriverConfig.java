package br.com.ornitorrincosoft.clear.webdriver;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {
	
	private WebDriver driver;

	@Value("${selenium.driver.name}")
	private String name;
	
	@Value("${selenium.driver.path}")	
	private String path;
	
	@Bean
	public WebDriver instance() {
		if("Chrome".equals(name)) {			
			System.setProperty("webdriver.chrome.driver", path);
			driver = new ChromeDriver();
		}
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return driver;
	}
	

}
