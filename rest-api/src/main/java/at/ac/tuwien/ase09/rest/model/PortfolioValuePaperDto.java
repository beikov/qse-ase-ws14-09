package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PortfolioValuePaperDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private ValuePaperDto valuePaperDto;
	private BigDecimal buyPrice;
	private int volume;
	
	public PortfolioValuePaperDto() { }
			
	public PortfolioValuePaperDto(long id, ValuePaperDto valuePaperDto,
			BigDecimal buyPrice, int volume) {
		super();
		this.id = id;
		this.valuePaperDto = valuePaperDto;
		this.buyPrice = buyPrice;
		this.volume = volume;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ValuePaperDto getValuePaperDto() {
		return valuePaperDto;
	}

	public void setValuePaperDto(ValuePaperDto valuePaperDto) {
		this.valuePaperDto = valuePaperDto;
	}

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	

}
