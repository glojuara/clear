package br.com.ornitorrincosoft.clear.services.indicators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.ornitorrincosoft.clear.models.Candle;

@Service
public class StochasticOscillatorServiceImp implements StochasticOscillatorService {

	@Override
	public Map<String, Double> calculateStochasticOscillator(double c, int n, List<Candle> candles) throws Exception {
//		TimeUnit.SECONDS.sleep(1);

		Map<String, Double> stochastic = new LinkedHashMap<>();
		stochastic.put("fast", calculateFastStochasticOscilator(c, n, candles));
		stochastic.put("soft", calculateSoftStochasticOscilator(n, candles));
		return stochastic;
	}

	private Double calculateFastStochasticOscilator(double c, int n, List<Candle> candles) {
		// n = 5
		// formula: 100 * [( c - l5 ) / ( h5 - l5)]
		if (candles.size() >= n) {
			Comparator<? super Candle> comparator = new Comparator<Candle>() {
				@Override
				public int compare(Candle c1, Candle c2) {
					return c2.getDateTime().compareTo(c1.getDateTime());
				}
			};
			
			List<Candle> subList = candles.stream().sorted(comparator).collect(Collectors.toList()).subList(0, n);
			
			double high = subList.stream().mapToDouble(candle -> candle.getHigh().doubleValue()).max().getAsDouble();
			double low = subList.stream().mapToDouble(candle -> candle.getLow().doubleValue()).min().getAsDouble();
			return 100 * ((c - low) / (high  - low));
		}
		return null;
	}
	
	private Double calculateSoftStochasticOscilator(int n, List<Candle> candles) {
		if (candles.size() >= n + 2) {
			List<Double> stochastics = new ArrayList<>();
			for (int i = 0; i < n - 2; i++) {
				List<Candle> subList = candles.subList(0, candles.size() - i);
				Double fastStochasticOscilator = calculateFastStochasticOscilator(subList.get(subList.size() - 1).getClose().doubleValue(), n, subList);
				stochastics.add(fastStochasticOscilator);
			}
			return stochastics.stream().mapToDouble(stochastic -> stochastic).average().getAsDouble();
		}
		return null;
	}
	
}
