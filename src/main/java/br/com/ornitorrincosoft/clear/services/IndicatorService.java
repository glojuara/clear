package br.com.ornitorrincosoft.clear.services;

import java.util.List;
import java.util.Map;

import br.com.ornitorrincosoft.clear.models.Candle;

public interface IndicatorService {

	Double relativeStrengthIndex(int n, List<Candle> candles) throws Exception;
	Map<String, Double> stochasticOscillator(double c, int n, List<Candle> candles) throws Exception;

}
