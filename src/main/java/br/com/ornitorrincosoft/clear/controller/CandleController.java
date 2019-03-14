package br.com.ornitorrincosoft.clear.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ornitorrincosoft.clear.models.Candle;
import br.com.ornitorrincosoft.clear.models.CandleWrapper;
import br.com.ornitorrincosoft.clear.models.Order;
import br.com.ornitorrincosoft.clear.models.Order.OrderStatus;
import br.com.ornitorrincosoft.clear.models.Order.OrderType;
import br.com.ornitorrincosoft.clear.services.CandleService;
import br.com.ornitorrincosoft.clear.services.indicators.RelativeStrengthIndexService;
import br.com.ornitorrincosoft.clear.services.indicators.StochasticOscillatorService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("candle")
@AllArgsConstructor
public class CandleController {

	private static final double PRICE = 0.2;
	private static final int CONTRACTS = 5;
	private static final int STOP_LOSS = 60;
	private static final int STOP_GAIN = 20;
	
	
	private CandleService candleService;
	private StochasticOscillatorService stochasticOscillator;
	private RelativeStrengthIndexService relativeStrengthIndex;

	@GetMapping("pagination/{page}/{size}")
	public Page<Candle> pagination(@PathVariable("page") int page, @PathVariable("size") int size ) {
		return candleService.pagination(page, size);
	}


	@GetMapping("all")
	public List<Candle> getAll() {
		return candleService.getAll();
	}

	@GetMapping("load/{month}/{day}")
	public ResponseEntity<?> load(@PathVariable("month") String month, @PathVariable("day") String day)
			throws Exception {
		List<Candle> filtered = loadCandles(month, day);
//		candleService.insert(candles.getCandles());
		return ResponseEntity.ok(filtered);
	}


	@GetMapping("charles/{month}/{day}")
	public ResponseEntity<?> charlesStrategy(@PathVariable("month") String month, @PathVariable("day") String day) throws Exception {

		List<Candle> allCandles = loadCandles(month, day);
		List<Candle> candles = new ArrayList<>();

		Integer loss = 0;
		Integer gain = 0;
		Order order = Order.builder().build();

		Double savedRsi = null;
		Map<String, Double> savedOscilators = null;

		
		Boolean cross = Boolean.FALSE;
		
		int savedIndicator = 0;
		
		Double lastFast = null;

		
		for (Candle candle : allCandles) {
			candles.add(candle);
			
			if (!isOpenOrder(order) && candles.size() >= 7) {
				Map<String, Double> oscillators = stochasticOscillator.calculateStochasticOscillator(candle.getClose().doubleValue(), 5, candles);
				Double rsi = relativeStrengthIndex.calculateRelativeStrengthIndex(14, candles);
				
				
			} else {

				if (OrderType.BUY.equals(order.getType())) {

					if (candle.getLow().compareTo(order.getStopLoss()) <= 0) {
						order.setStatus(OrderStatus.LOSS);
						loss++;

						System.out.println();
						System.out.println("STOP LOSS [COMPRA]: " + candle.getDateTime());
						savedOscilators.entrySet().stream().forEach(entry -> {
							String o = new StringBuilder(entry.getKey()).append(": ").append(entry.getValue()).toString();
							System.out.println(o);
						} );
						System.out.println("RSI: " + savedRsi);
						System.out.println();

					} else if (candle.getHigh().compareTo(order.getStopGain()) >= 0) {
						order.setStatus(OrderStatus.GAIN);
						gain++;

						System.out.println();
						System.out.println("STOP GAIN [COMPRA]: " + candle.getDateTime());
						savedOscilators.entrySet().stream().forEach(entry -> {
							String o = new StringBuilder(entry.getKey()).append(": ").append(entry.getValue()).toString();
							System.out.println(o);
						} );
						System.out.println("RSI: " + savedRsi);
						System.out.println();
					}

				} else if (OrderType.SELL.equals(order.getType())) {

					if (candle.getHigh().compareTo(order.getStopLoss()) >= 0) {
						order.setStatus(OrderStatus.LOSS);
						loss++;
						
						System.out.println();
						System.out.println("STOP LOSS [VENDA]: " + candle.getDateTime());
						savedOscilators.entrySet().stream().forEach(entry -> {
							String o = new StringBuilder(entry.getKey()).append(": ").append(entry.getValue()).toString();
							System.out.println(o);
						} );
						System.out.println("RSI: " + savedRsi);
						System.out.println();

					} else if (candle.getLow().compareTo(order.getStopGain()) <= 0) {
						order.setStatus(OrderStatus.GAIN);
						gain++;
						
						System.out.println();
						System.out.println("STOP GAIN [VENDA]: " + candle.getDateTime());
						savedOscilators.entrySet().stream().forEach(entry -> {
							String o = new StringBuilder(entry.getKey()).append(": ").append(entry.getValue()).toString();
							System.out.println(o);
						} );
						System.out.println("RSI: " + savedRsi);
						System.out.println();
					}
				}
			}
			System.gc();
		}

		summarize(loss, gain);

		return ResponseEntity.ok().build();
	}

	private void summarize(Integer loss, Integer gain) {
		double totalLoss = (loss * STOP_LOSS) * PRICE * CONTRACTS;
		double totalGain = (gain * STOP_GAIN) * PRICE * CONTRACTS;
		System.out.println();
		System.out.println("############################################");
		System.out.println("$$$$$$         EXTRATO DO DIA         $$$$$$");
		System.out.println("############################################");
		System.out.println();
		System.out.println("LOSS [ " + loss + " ] - R$ " + totalLoss);
		System.out.println("GAIN [ " + gain + " ] - R$ " + totalGain);
		System.out.println("BALANCE R$ " + (totalGain - totalLoss));
	}

	private Order buy(Candle candle) {
		return Order.builder().quantity(CONTRACTS).price(candle.getClose())
				.stopLoss(candle.getClose().subtract(BigDecimal.valueOf(STOP_LOSS)))
				.stopGain(candle.getClose().add(BigDecimal.valueOf(STOP_GAIN))).type(OrderType.BUY).status(OrderStatus.OPEN)
				.build();
	}

	private Order sell(Candle candle) {
		return Order.builder().quantity(CONTRACTS).price(candle.getClose())
				.stopLoss(candle.getClose().add(BigDecimal.valueOf(STOP_LOSS)))
				.stopGain(candle.getClose().subtract(BigDecimal.valueOf(STOP_GAIN))).type(OrderType.SELL)
				.status(OrderStatus.OPEN).build();
	}

	private List<Candle> loadCandles(String month, String day) throws Exception {
		
		String fileName = new StringBuilder("classpath:")
			.append("candles_")
			.append(month)
			.append("_")
			.append(day)
			.append(".json").toString();

		File file = ResourceUtils.getFile(fileName);
		
		ObjectMapper mapper = new ObjectMapper();
		CandleWrapper candles = mapper.readValue(file, CandleWrapper.class);
		
		System.out.println(file.getAbsolutePath());

		List<Candle> filtered = candles.getCandles().stream().filter(candle -> {
			LocalDateTime dateTime = candle.getDateTime();
			if (dateTime.getMonth().getValue() == Integer.parseInt(month)) {
				if (dateTime.getDayOfMonth() == Integer.parseInt(day)) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		return filtered;
	}
	
	private Integer candleVariation(Candle candle) {
		return candle.getClose().compareTo(candle.getOpen());
	}

	private Boolean isOpenOrder(Order order) {
		return Order.OrderStatus.OPEN.equals(order.getStatus());
	}


}
