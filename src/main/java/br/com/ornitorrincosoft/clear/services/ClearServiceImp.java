package br.com.ornitorrincosoft.clear.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ornitorrincosoft.clear.models.Order;
import br.com.ornitorrincosoft.clear.models.Order.OrderStatus;
import br.com.ornitorrincosoft.clear.models.Order.OrderType;

@Service
public class ClearServiceImp implements ClearService {
	
	private static final String COMPRA = "compra";
	private static final String VENDA = "venda";

	private static Boolean onFrame = false;
	private List<Order> orders = new ArrayList<>();

	@Autowired
	private WebDriver driver;
	
	@Override
	public BigDecimal getAssetPrice() {
		switchToFrame();
		WebElement element = driver.findElement(By.className("asset-price"));
		return this.format(element);
	}

	@Override
	public BigDecimal getSellPrice() {
		switchToFrame();
		WebElement element = driver.findElement(By.cssSelector(".book .container_b_right .cont_third_book label:nth-child(1) span a:nth-child(2)"));
		return this.format(element);
	}

	@Override
	public BigDecimal getBuyPrice() {
		switchToFrame();
		WebElement element = driver.findElement(By.cssSelector(".book .container_b_left .cont_third_book label:nth-child(10) span a:nth-child(2)"));
		return this.format(element);
	}
	
	@Override
	public String getBalance() {
		switchToFrame();
		driver.findElement(By.cssSelector(".container_views .views_header ul li:nth-child(1)")).click();
		driver.findElement(By.cssSelector(".container_views .views_header ul li:nth-child(2)")).click();
		return driver.findElement(By.cssSelector(".container_balance .balance-d0-value")).getText();
	}
	
	private BigDecimal format(WebElement element) {
		String value = element.getText().replace(".", "");
		return new BigDecimal(value);
	}

	@Override
	public String getProfitLoss() {
		switchToFrame();
		return driver.findElement(By.cssSelector(".daytrade .container_dt_four .summary-profitloss")).getText();
	}
	
	@Override
	public Order buy(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain) {
		Order order = buildOrder(price, stopLoss, stopGain, OrderType.BUY);
		return buy(order);
	}

	@Override
	public Order sell(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain) {
		Order order = buildOrder(price, stopLoss, stopGain, OrderType.SELL);
		return sell(order);
	}
	
	private Order buildOrder(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain, OrderType type) {		
		return Order.builder()
			.price(price)
			.stopLoss(stopLoss)
			.stopGain(stopGain)
			.type(type)
			.status(OrderStatus.OPEN)
			.build();
	}
	
	private void setLimit(WebDriver driver, BigDecimal price, String op) {
		WebElement quantity = driver.findElement(By.cssSelector(".form-oto.daytrade-" + op + "-oto #input-quantity"));
		quantity.clear();
		quantity.sendKeys("1");
		
		WebElement limit = driver.findElement(By.cssSelector(".form-oto.daytrade-" + op + "-oto #input-price-limit"));
		limit.clear();
		limit.sendKeys(price.toPlainString());
	}

	private void setStopLoss(WebDriver driver, BigDecimal price, String op) {
		WebElement stopLoss = driver.findElement(By.cssSelector(".form-oto.daytrade-" + op + "-oto #input-price-loss-stop"));
		stopLoss.clear();
		stopLoss.sendKeys(price.toPlainString());
	}

	private void setStopGain(WebDriver driver, BigDecimal price, String op) {
		WebElement stopGain = driver.findElement(By.cssSelector(".form-oto.daytrade-" + op + "-oto #input-price-gain-stop"));
		stopGain.clear();
		stopGain.sendKeys(price.toPlainString());
	}

	private void selectStrategy() {
		Select strategy = new Select(driver.findElement(By.id("choose-strategy")));
		strategy.selectByVisibleText("O.T.O.");
	}
	
	private void submit(BigDecimal price, BigDecimal stopLoss, BigDecimal stopGain, String op) {
		this.selectStrategy();
		this.setLimit(driver, price, op);
		this.setStopLoss(driver, stopLoss, op);
		this.setStopGain(driver, stopGain, op);
		driver.findElement(By.cssSelector(".buy-sell-label button")).click();
	}

	@Override
	public void resetAll() {
		WebElement btnOrders = driver.findElement(By.cssSelector(".container_views .views_header ul li:nth-child(1) a"));
		btnOrders.click();
		driver.findElement(By.className("bt_reset_all")).click();
		driver.findElement(By.cssSelector(".container_action_orders .assinatura_cta.orders .action-reset-all")).click();
		
		this.orders.stream()
			.filter(order -> OrderStatus.OPEN.equals(order.getStatus()))
			.forEach(order -> order.setStatus(OrderStatus.LOSS));
		
	}

	@Override
	public List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public Order buy(Order order) {
		WebElement btnBuy = driver.findElement(By.className("bt_buy"));
		btnBuy.click();
		submit(order.getPrice(), order.getStopLoss(), order.getStopGain(), COMPRA);
		orders.add(order);
		return order;
	}

	@Override
	public Order sell(Order order) {
		WebElement btnSell = driver.findElement(By.className("bt_sell"));
		btnSell.click();
		submit(order.getPrice(), order.getStopLoss(), order.getStopGain(), VENDA);
		orders.add(order);
		return order;
	}
	
	private void switchToFrame() {
		if (!onFrame) {			
			driver.switchTo().defaultContent();
			driver.switchTo().frame("content-page");
			onFrame = true;
		}
	}



}
