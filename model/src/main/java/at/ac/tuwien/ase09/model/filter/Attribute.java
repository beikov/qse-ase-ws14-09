package at.ac.tuwien.ase09.model.filter;


public enum Attribute {
	
	PRICE(AttributeType.NUMERIC, "PRICE (Preis)"),
	MARKET_CAP(AttributeType.NUMERIC, "MARKET_CAP (Börsenwert)"),
	ENTERPRISE_VALUE(AttributeType.NUMERIC, "ENTERPRISE_VALUE (Unternehmenswert)"),
	TRAILING_PE(AttributeType.NUMERIC, ""),
	FORWARD_PE(AttributeType.NUMERIC, ""),
	PEG_RATIO(AttributeType.NUMERIC, ""),
	PRICE_SALES(AttributeType.NUMERIC, "PRICE_SALES (Kurs-Umsatz-Verhältnis)"),
	PRICE_BOOK(AttributeType.NUMERIC, "PRICE_BOOK (Kurs-Buchwert-Verhältnis)"),
	ENTERPRISE_VALUE_REVENUE(AttributeType.NUMERIC, "ENTERPRISE_VALUE_REVENUE (Unternehmenswert zu Umsatz)"),
	ENTERPRISE_VALUE_EBITDA(AttributeType.NUMERIC, "ENTERPRISE_VALUE_EBITDA (Unternehmenswert zu EBITDA)"),
	PROFIT_MARGIN(AttributeType.NUMERIC, "PROFIT_MARGIN (Gewinnspanne)"),
	OPERATING_MARGIN(AttributeType.NUMERIC, ""),
	RETURN_ON_ASSETS(AttributeType.NUMERIC, "RETURN_ON_ASSETS (Vermögensrendite)"),
	RETURN_ON_EQUITY(AttributeType.NUMERIC, "RETURN_ON_EQUITY (Eigenkapitalrendite)"),
	REVENUE(AttributeType.NUMERIC, "REVENUE (Umsatz)"),
	REVENUE_PER_SHARE(AttributeType.NUMERIC, ""),
	QTRLY_REVENUE_GROWTH(AttributeType.NUMERIC, ""),
	GROSS_PROFIT(AttributeType.NUMERIC, "GROSS_PROFIT (Bruttogewinn)"),
	EBITDA(AttributeType.NUMERIC, "EBITDA (operativer Gewinn)"),
	NET_INCOME_AVL_TO_COMMON(AttributeType.NUMERIC, ""),
	DILUTED_EPS(AttributeType.NUMERIC, ""),
	QTRLY_EARNINGS_GROWTH(AttributeType.NUMERIC, ""),
	TOTAL_CASH(AttributeType.NUMERIC, "TOTAL_CASH (Totaler Cash-Flow)"),
	TOTAL_CASH_PER_SHARE(AttributeType.NUMERIC, "TOTAL_CASH_PER_SHARE (Totaler Cash-Flow/Aktie)"),
	TOTAL_DEBT(AttributeType.NUMERIC, "TOTAL_DEBT (Gesamtverschuldung)"),
	TOTAL_DEBT_EQUITY(AttributeType.NUMERIC, "TOTAL_DEBT_EQUITY (Eigenkapitalüberdeckung)"),
	CURRENT_RATIO(AttributeType.NUMERIC, ""),
	BOOK_VALUE_PER_SHARE(AttributeType.NUMERIC, ""),
	OPERATING_CASH_FLOW(AttributeType.NUMERIC, "OPERATING_CASH_FLOW (Operativer Cash-Flow)"),
	LEVERED_FREE_CASH_FLOW(AttributeType.NUMERIC, ""),
	BETA(AttributeType.NUMERIC, "BETA (Betafaktor)"),
	P52_WEEK_CHANGE(AttributeType.NUMERIC, "52_WEEK_CHANGE (52-Wochen-Änderung)"),
	P52_WEEK_HIGH(AttributeType.NUMERIC, "52_WEEK_HIGH (52-Wochen-Hoch)"),
	P52_WEEK_LOW(AttributeType.NUMERIC, "52_WEEK_LOW (52-Wochen-Tief)"),
	P50_DAY_MOVING_AVERAGE(AttributeType.NUMERIC, ""),
	P200_DAY_MOVING_AVERAGE(AttributeType.NUMERIC, ""),
	AVG_VOL_3_MONTH(AttributeType.NUMERIC, "AVG_VOL_3_MONTH (Durchschn. Handelsvolumen(3 Monate))"),
	AVG_VOL_10_DAY(AttributeType.NUMERIC, "AVG_VOL_10_DAY (Durchschn. Handelsvolumen(10 Tage))"),
	SHARES_OUTSTANDING(AttributeType.NUMERIC, "SHARES_OUTSTANDING (Aktien im Umlauf)"),
	FLOAT(AttributeType.NUMERIC, "FLOAT (Wert der Aktien in Streubesitz)"),
	PERCENTAGE_HELD_BY_INSIDERS(AttributeType.NUMERIC, "PERCENTAGE_HELD_BY_INSIDERS (% gehalten von Insidern)"),
	PERCENTAGE_HELD_BY_INSTITUTIONS(AttributeType.NUMERIC, "PERCENTAGE_HELD_BY_INSTITUTIONS (% gehlaten von Institutionen)"),
	SHARES_SHORT_CURRENT_MONTH(AttributeType.NUMERIC, "SHARES_SHORT (Aktien im Shortverkauf(akt. Monat))"),
	SHORT_RATIO(AttributeType.NUMERIC, ""),
	SHORT_PERCENTAGE_OF_FLOAT(AttributeType.NUMERIC, ""),
	SHARES_SHORT_PRIOR_MONTH(AttributeType.NUMERIC, ""),
	FORWARD_ANNUAL_DIVIDEND_RATE(AttributeType.NUMERIC, ""),
	FORWARD_ANNUAL_DIVIDEND_YIELD(AttributeType.NUMERIC, ""),
	TRAILING_ANNUAL_DIVIDEND_YIELD_ABSOLUTE(AttributeType.NUMERIC, ""),
	TRAILING_ANNUAL_DIVIDEND_YIELD_RELATIVE(AttributeType.NUMERIC, ""),
	P5_YEAR_AVERAGE_DIVIDEND_YIELD(AttributeType.NUMERIC, "5_YEAR_AVERAGE_DIVIDEND_YIELD (Durchschn. Dividendenrendite(5 Jahre))"),
	PAYOUT_RATIO(AttributeType.NUMERIC, "PAYOUT_RATIO (Auszahlungskurs)"),

	FISCAL_YEAR_ENDS(AttributeType.DATE_TIME, ""),
	MOST_RECENT_QUARTER(AttributeType.DATE_TIME, ""),
	DIVIDEND_DATE(AttributeType.DATE_TIME, ""),
	EX_DIVIDEND_DATE(AttributeType.DATE_TIME, ""),

	NAME(AttributeType.STRING, "Name"),
	CODE(AttributeType.STRING, "Code/ISIN"),
	INDEX(AttributeType.STRING, "Aktienindex"),
	COUNTRY(AttributeType.STRING, "Land"),
	CURRENCY(AttributeType.STRING, "Währung");

	
	private final AttributeType attributeType;
	private final String label;
	private final String pwatchName;
	
	private Attribute(AttributeType attributeType, String label) {
		this.attributeType = attributeType;
		
		String pwatchName = name();
		
		if (pwatchName.charAt(0) == 'P' && Character.isDigit(pwatchName.charAt(1))) {
			pwatchName = pwatchName.substring(1);
		}
		
		this.pwatchName = pwatchName;
		
		if (label.isEmpty()) {
			this.label = pwatchName;
		} else {
			this.label = label;
		}
	}

	public AttributeType getAttributeType() {
		return attributeType;
	}

	public String getLabel() {
		return label;
	}

	public String getPwatchName() {
		return pwatchName;
	}

	public static Attribute valueOfPwatch(String text) {
		if (Character.isDigit(text.charAt(0))) {
			return valueOf("P" + text);
		}
		
		return valueOf(text);
	}

}
