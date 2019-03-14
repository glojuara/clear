package br.com.ornitorrincosoft.clear.repositories;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.ornitorrincosoft.clear.models.Candle;

public interface CandleRepository extends MongoRepository<Candle, ObjectId>, Serializable{
	
	List<Candle> findByOpenGreaterThan(BigDecimal open);
	List<Candle> findByOpenLessThan(BigDecimal open);

}
