package br.com.ornitorrincosoft.clear.services.indicators;

import java.util.List;

import br.com.ornitorrincosoft.clear.models.Candle;

public interface RelativeStrengthIndexService {
	Double calculateRelativeStrengthIndex(int n, List<Candle> candles) throws Exception;
	
}
