package at.ac.tuwien.ase09.filter;


public enum AttributeType {
	
	
	NAME("name","Name",false),
	CODE( "code","Code/Isin",false),
	
	COUNTRY("country","Land",false),
	INDEX("index","Aktienindex",false),
	CURRENCY( "currency","Währung",false),
	MARKETCAP("marketCap","MARKETCAP (Börsenwert)",true),
	ENTERPRISEVALUE("enterpriseValue","ENTERPRISEVALUE(Unternehmenswert)",true),
	PRICESALES("priceSales","PRICESALES (Kurs-Umsatz-Verhältnis)",true),
	PRICEBOOK("priceBook","PRICEBOOK (Kurs-Buchwert-Verhältnis)",true),
	ENTERPRISEVALUEREVENUE("enterpriseValueRevenue","ENTERPRISEVALUEREVENUE (Unternehmenswert zu Umsatz)",true),
	ENTERPRISEVALUEEBITDA("enterpriseValueEBITDA","ENTERPRISEVALUEEBITDA (Unternehmenswert zu EBITDA)", true),
	PROFITMARGIN("profitMargin","PROFITMARGIN (Gewinnspanne)",true),
	RETURNONASSETS("returnonAssets","RETURNONASSETS (Vermögensrendite)",true),
	RETURNONEQUITY("returnonEquity","RETURNONEQUITY (Eigenkapitalrendite)",true),
	REVENUE("revenue","REVENUE (Umsatz)",true),
	GROSSPROFIT("grossProfit","GROSSPROFIT (Bruttogewinn)",true),
	EBITDA("ebitda","EBITDA(operativer Gewinn)",true),
	TOTALCASH("totalCash","TOTALCASH (Totaler Cash-Flow)",true),
	TOTALCASHSHARE("totalCashPerShare","TOTALCASHSHARE (Totaler Cash-Flow/Aktie)",true),
	TOTALDEBT("totalDebt","TOTALDEBT (Gesamtverschuldung)",true),
	TOTALDEBTEQUITY("totalDebtEquity","TOTALDEBTEQUITY (Eigenkapitalüberdeckung)",true),
	OPERATINGCASHFLOW("operatingCashFlow","OPERATINGCASHFLOW (Operativer Cash-Flow)",true),
	BETA("beta","BETA (Betafaktor)",true),
	WEEKCHANGE("p_52_WeekChange","WEEKCHANGE (52-Wochen-Änderung)",true),
	WEEKHIGH("p_52_WeekHigh","WEEKHIGH (52-Wochen-Hoch)",true),
	WEEKLOW("p_52_WeekLow","WEEKLOW (52-Wochen-Tief)",true),
	AVGVOLUME3M("avgVol_3month","AVGVOLUME3M (Durchschn. Handelsvolumen(3 Monate))",true),
	AVGVOLUME10D("avgVol_10day","AVGVOLUME10DDurchschn. Handelsvolumen(10 Tage))",true),
	SHARESOUTSTANDING("sharesOutstanding","SHARESOUTSTANDING (Aktien im Umlauf)",true),
	FLOATVALUE("floatVal","FLOATVALUE (Wert der Aktien in Streubesitz)",true),
	PERCINSIDERS("percentageHeldbyInsiders","PERCINSIDERS (% gehalten von Insidern)",true),
	PERCINSTIT("percentageHeldbyInstitutions","PERCINSTIT (% gehlaten von Institutionen)",true),
	SHARESSHORT("sharesShortCurrentMonth","SHARESSHORT (Aktien im Shortverkauf(akt. Monat))",true),
	DIVIDENDYIELD5("p_5YearAverageDividendYield","DIVIDENDYIELD5 (Durchschn. Dividendenrendite(5 Jahre))",true),
	PAYOUTRATIO("payoutRatio","PAYOUTRATIO (Auszahlungskurs)",true);

	
	 private final String parmName,label;
	 private final Boolean numeric;

	   AttributeType( String parmName,String label,boolean numeric ) { 
		   this.parmName = parmName; 
		   this.label=label;
		   this.numeric=numeric;
		   }

	   public String getParmName() { return parmName; }
	   
	   public String getLabel() { return label; }
	   
	   public Boolean isNumeric() { return numeric; }

}
