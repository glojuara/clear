package br.com.ornitorrincosoft.clear.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document(collection = "candles")
@NoArgsConstructor
@AllArgsConstructor
public class Candle {
	
	@Id
	@Field("_id")
	private ObjectId id;

	@JsonProperty("T")
	private LocalDateTime dateTime;
	
	@JsonProperty("O")
	private BigDecimal open;
	
	@JsonProperty("C")
	private BigDecimal close;
	
	@JsonProperty("H")
	private BigDecimal high;
	
	@JsonProperty("L")
	private BigDecimal low;
	
	@JsonProperty("V")
	private BigDecimal volume;
	
	public void setDateTime(long time) {
		Timestamp timestamp = new Timestamp(time * 1000);
		this.dateTime = timestamp.toLocalDateTime();
	}
	
}
