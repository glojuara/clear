package br.com.ornitorrincosoft.clear.bot;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import br.com.ornitorrincosoft.clear.App;
import br.com.ornitorrincosoft.clear.bot.actions.BotAction;
import br.com.ornitorrincosoft.clear.bot.actions.StartAction;
import br.com.ornitorrincosoft.clear.bot.actions.StopAction;
import br.com.ornitorrincosoft.clear.bot.actions.WatchAction;
import br.com.ornitorrincosoft.clear.services.ClearService;

//@Component
public class BuriloBot extends TelegramLongPollingBot {

	private static final String MONEY = "/money";
	private static final String BALANCE = "/balance";
	private static final String WATCH = "/watch";
	private static final String RUN = "/run";
	private static final String START = "/start";
	private static final String STOP = "/stop";

	public static Boolean RUNNING = false;

	@Value("${telegram.bot.userName}")
	private String userName;

	@Value("${telegram.bot.token}")
	private String token;

	@Override
	public void onUpdateReceived(Update update) {

		if (update.hasMessage()) {
			Message message = update.getMessage();
			SendMessage sender = null;

			String response = null;
			BotAction action = null;
			ClearService clearService = App.context.getBean(ClearService.class);

			switch (message.getText()) {
			case START:
				response = new StringBuilder().append("Olá, ").append(message.getFrom().getFirstName())
						.append(" o que deseja?").toString();
				action = App.context.getBean(StartAction.class);
				break;

			case RUN:
				response = new StringBuilder().append("Ok, vamos la... ").append("Estou iniciando o processo, ")
						.append("certifique-se de estar logado e na pagina de cotações. ")
						.append("Em caso positivo clique em ").append(WATCH).toString();

				break;

			case WATCH:
				response = new StringBuilder().append("Ótimo, estou analisando o mercado, ")
						.append("assim que encontrar alguma oportunidade te aviso. ")
						.append("Caso queira saber o seu lucro ou prejuízo digite ").append(MONEY).append(", ")
						.append("Para encerrar execute o comando ").append(STOP).toString();

				action = App.context.getBean(WatchAction.class);
				break;

			case MONEY:
				response = new StringBuilder().append("Seu lucro / prejuízo  é de: ").append(clearService.getProfitLoss()).toString();
				break;
			
			case BALANCE:
				response = new StringBuilder().append("Seu saldo atual é de: ").append(clearService.getBalance()).toString();
				break;

			case STOP:
				response = new StringBuilder().append("Encerrando operações ")
						.append("caso queira iniciar novamente é só chamar ou clicar em /run. ").append("Até breve ")
						.append(message.getFrom().getFirstName()).toString();

				action = App.context.getBean(StopAction.class);
				break;

			default:
				response = new StringBuilder().append("Você pode executar os seguintes comandos: ")
						.append(RUN).append(" - Iniciar o processo (autenticado), ")
						.append(WATCH).append(" - Analisar mercado, ")
						.append(MONEY).append(" - Exibir lucros ou prejuízos, ")
						.append(BALANCE).append(" - Exibir saldo atual, ")
						.append(STOP).append(" - Parar o processo")
						.toString();
				break;
			}

			try {

				final BotAction botAction = action;
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (botAction != null) {
							try {
								botAction.run(message);
							} catch (Exception e) {}
						}

					}
				}).start();

				sender = new SendMessage(message.getChatId(), response);
				execute(sender);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public String getBotUsername() {
		return this.userName;
	}

	@Override
	public String getBotToken() {
		return this.token;
	}

}
