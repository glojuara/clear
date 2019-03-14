package br.com.ornitorrincosoft.clear.bot.actions;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StartAction implements BotAction {

	private WebDriver driver;

	@Override
	public void run(Message message) {

		driver.manage().window().maximize();
		driver.get("https://www.clear.com.br/pit/signin");

	}

}
