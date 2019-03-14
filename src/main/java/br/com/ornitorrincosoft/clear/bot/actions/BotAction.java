package br.com.ornitorrincosoft.clear.bot.actions;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface BotAction {
	
	void run(Message message) throws Exception;

}
