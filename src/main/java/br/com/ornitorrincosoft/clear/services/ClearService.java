package br.com.ornitorrincosoft.clear.services;

import java.math.BigDecimal;
import java.util.List;

import br.com.ornitorrincosoft.clear.models.Order;

public interface ClearService {

	BigDecimal getAssetPrice();	
	BigDecimal getSellPrice();	
	BigDecimal getBuyPrice();	
	String getBalance();
	String getProfitLoss();
	Order buy(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain);
	Order sell(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain);
	void resetAll();
	Order buy(Order order);
	List<Order> getOrders();
	Order sell(Order order);
}
