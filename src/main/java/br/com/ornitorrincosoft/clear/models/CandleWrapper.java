package br.com.ornitorrincosoft.clear.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CandleWrapper {
	
	@JsonProperty("data")
	private List<Candle> candles;
	
}
