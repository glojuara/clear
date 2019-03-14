package br.com.ornitorrincosoft.clear.bot.actions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import br.com.ornitorrincosoft.clear.bot.BuriloBot;
import br.com.ornitorrincosoft.clear.models.Candle;
import br.com.ornitorrincosoft.clear.services.CandleService;
import br.com.ornitorrincosoft.clear.services.ClearService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WatchAction implements BotAction {

	private ClearService clearService;
	private CandleService candleService;

	@Override
	public void run(Message message) throws Exception {
		BuriloBot.RUNNING = true;

		LocalTime now = LocalTime.now();
		int second = now.getSecond();
		int minute = now.getMinute();
		Candle candle = null;

		while (BuriloBot.RUNNING) {
			TimeUnit.MILLISECONDS.sleep(100);

			now = LocalTime.now();

			BigDecimal assetPrice = clearService.getAssetPrice();
			Integer up = 0;
			Integer down = 0;

			if (second != now.getSecond()) {
				second = now.getSecond();

				if (second == 0) {
					if (candle != null) {
						candle.setClose(assetPrice);
						
						if (candle.getClose().compareTo(candle.getOpen()) > 0) {
							up++;
							down = 0;
							if (up == 4) {
								
							}
						} else if (candle.getClose().compareTo(candle.getOpen()) < 0) {
							up = 0;
							down++;
						} else {
							up = 0;
							down = 0;
						}
						
						candleService.insert(candle);
					}
					candle = Candle.builder().open(assetPrice).high(assetPrice).low(assetPrice)
							.dateTime(LocalDateTime.now()).build();
				}

			}

			if (minute != now.getMinute()) {
				minute = now.getMinute();
			} else {

				if (candle != null) {
					if (assetPrice.compareTo(candle.getLow()) < 0) {
						candle.setLow(assetPrice);
					} else if (assetPrice.compareTo(candle.getHigh()) > 0) {
						candle.setHigh(assetPrice);
					}
				}

			}
		}

	}

}
