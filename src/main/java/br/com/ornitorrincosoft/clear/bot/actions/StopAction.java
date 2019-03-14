package br.com.ornitorrincosoft.clear.bot.actions;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import br.com.ornitorrincosoft.clear.bot.BuriloBot;

@Component
public class StopAction implements BotAction {

	@Override
	public void run(Message message) {
		BuriloBot.RUNNING = false;
	}

}
