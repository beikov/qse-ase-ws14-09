package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;

public class PortfolioDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private MoneyDto currentCapital;

	public PortfolioDto(){}
	
	public PortfolioDto(Long id, String name, MoneyDto currentCapital) {
		super();
		this.id = id;
		this.name = name;
		this.currentCapital = currentCapital;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MoneyDto getCurrentCapital() {
		return currentCapital;
	}
	
	public void setCurrentCapital(MoneyDto currentCapital) {
		this.currentCapital = currentCapital;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
