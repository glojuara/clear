package br.com.ornitorrincosoft.clear.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.ornitorrincosoft.clear.models.Candle;
import br.com.ornitorrincosoft.clear.repositories.CandleRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CandleServiceImp implements CandleService {
	
	private CandleRepository candleRepository;
	
	@Override
	public List<Candle> getAll() {
		Sort sortByDateTime = new Sort(Direction.DESC, "dateTime");
		return candleRepository.findAll(sortByDateTime);	
	}

	@Override
	public Candle insert(Candle candle) {
		return candleRepository.insert(candle);
	}

	@Override
	public List<Candle> insert(List<Candle> candles) {
		return candleRepository.insert(candles);
	}

	@Override
	public Page<Candle> pagination(int page, int size) {
		return candleRepository.findAll(PageRequest.of(page, size));
	}
	

}
