package br.com.ornitorrincosoft.clear.services.indicators;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.ornitorrincosoft.clear.models.Candle;

@Service
public class RelativeStrengthIndexServiceImp implements RelativeStrengthIndexService {

	/*
	 * Entre 0 e 30, o RSI indica uma zona de sobrevenda. Caberá, portanto, evitar
	 * vender em tal período. Entre 70 e 100, indica uma zona de sobrecompra. Melhor
	 * evitar comprar em tais momentos. Entre 30 e 70, ele não dá nenhuma informação
	 * específica.
	 * 
	 */

	@Override
	public Double calculateRelativeStrengthIndex(int n, List<Candle> candles) {
		
		List<Candle> up = candles.stream().filter(candle -> candle.getClose().compareTo(candle.getOpen()) > 0)
				.collect(Collectors.toList());
		List<Candle> down = candles.stream().filter(candle -> candle.getClose().compareTo(candle.getOpen()) < 0)
				.collect(Collectors.toList());
		
		if (up.size() < n || down.size() < n) {
			return null;
		}

		Collections.reverse(up);
		Collections.reverse(down);
		
		double u = up.subList(0, n).stream()
				.mapToDouble(candle -> candle.getClose().subtract(candle.getOpen()).abs().doubleValue()).average()
				.getAsDouble();
		double d = down.subList(0, n).stream()
				.mapToDouble(candle -> candle.getClose().subtract(candle.getOpen()).abs().doubleValue()).average()
				.getAsDouble();

		double rs = u / d;

		return 100 - (100 / (1 + rs));

	}

}
