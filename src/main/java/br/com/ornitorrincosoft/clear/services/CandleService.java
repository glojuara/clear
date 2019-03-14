package br.com.ornitorrincosoft.clear.services;

import java.util.List;

import org.springframework.data.domain.Page;

import br.com.ornitorrincosoft.clear.models.Candle;

public interface CandleService {

	public List<Candle> getAll();
	public Candle insert(Candle candle);
	public List<Candle> insert(List<Candle> candles);
	public Page<Candle> pagination(int page, int size);
}
