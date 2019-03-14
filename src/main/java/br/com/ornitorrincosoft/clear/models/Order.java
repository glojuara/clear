package br.com.ornitorrincosoft.clear.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Order {
	
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal stopGain;
	private BigDecimal stopLoss;
	private OrderType type;
	private OrderStatus status;

	public enum OrderType {
		SELL, BUY
	}
	public enum OrderStatus {
		OPEN, GAIN, LOSS
	}
	
	
}
