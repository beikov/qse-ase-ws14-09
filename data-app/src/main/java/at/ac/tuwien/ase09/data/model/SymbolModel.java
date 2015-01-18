package at.ac.tuwien.ase09.data.model;

import java.io.Serializable;

public class SymbolModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String symbol;
	private final String name;
	
	public SymbolModel(String symbol, String name) {
		super();
		this.symbol = symbol;
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getName() {
		return name;
	}
	
}
