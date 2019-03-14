package br.com.ornitorrincosoft.clear.services.indicators;

import java.util.List;
import java.util.Map;

import br.com.ornitorrincosoft.clear.models.Candle;

public interface StochasticOscillatorService {

	Map<String, Double> calculateStochasticOscillator(double c, int n, List<Candle> candles) throws Exception;
	
}
