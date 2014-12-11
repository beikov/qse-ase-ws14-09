package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;

import at.ac.tuwien.ase09.model.Money;

public class PortfolioDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Money currentCapital;

	public PortfolioDto(String name, Money currentCapital) {
		super();
		this.name = name;
		this.currentCapital = currentCapital;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Money getCurrentCapital() {
		return currentCapital;
	}
	
	public void setCurrentCapital(Money currentCapital) {
		this.currentCapital = currentCapital;
	}
	
}
