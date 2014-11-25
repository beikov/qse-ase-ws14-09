package at.ac.tuwien.ase09.filter;


public enum AttributeType {
	
	
	NAME("name","Name",false),
	CODE( "code","Code/Isin",false),
	
	CURRENCY( "currency","Währungscode",false),
	MARKETCAP("marketCap","Börsenwert",true),
	ENTERPRISEVALUE("enterpriseValue","Unternehmenswert",true),
	PRICESALES("priceSales","Kurs-Umsatz-Verhältnis",true),
	PRICEBOOK("priceBook","Kurs-Buchwert-Verhältnis",true),
	ENTERPRISEVALUEREVENUE("enterpriseValueRevenue","Unternehmenswert zu Umsatz",true),
	ENTERPRISEVALUEEBITDA("enterpriseValueEBITDA","Unternehmenswert zu EBITDA", true),
	PROFITMARGIN("profitMargin","Gewinnspanne",true),
	RETURNONASSETS("returnonAssets","Vermögensrendite",true),
	RETURNONEQUITY("returnonEquity","Eigenkapitalrendite",true),
	REVENUE("revenue","Umsatz",true),
	GROSSPROFIT("grossProfit","Bruttogewinn",true),
	EBITDA("ebitda","EBITDA(operativer Gewinn)",true),
	TOTALCASH("totalCash","Totaler Cash-Flow",true),
	TOTALCASHSHARE("totalCashPerShare","Totaler Cash-Flow/Aktie",true),
	TOTALCASHPERSHARE("totalDebt","Gesamtverschuldung",true),
	TOTALDEBTEQUITY("totalDebtEquity","Eigenkapitalüberdeckung",true),
	OPERATINGCASHFLOW("operatingCashFlow","Operativer Cash-Flow",true),
	BETA("beta","Betafaktor",true),
	WEEKCHANGE("p_52_WeekChange","52-Wochen-Änderung",true),
	WEEKHIGH("p_52_WeekHigh","52-Wochen-Hoch",true),
	WEEKLOW("p_52_WeekLow","52-Wochen-Tief",true),
	AVGVOLUME3M("avgVol_3month","Durchschn. Handelsvolumen(3 Monate)",true),
	AVGVOLUME10D("avgVol_10day","Durchschn. Handelsvolumen(10 Tage)",true),
	SHARESOUTSTANDING("sharesOutstanding","Aktien im Umlauf",true),
	FLOATVALUE("floatVal","Wert der Aktien in Streubesitz",true),
	PERCINSIDERS("percentageHeldbyInsiders","% gehalten von Insidern",true),
	PERCINSTIT("percentageHeldbyInstitutions","% gehlaten von Institutionen",true),
	SHARESSHORT("sharesShortCurrentMonth","Aktien im Shortverkauf(akt. Monat)",true),
	DIVIDENDYIELD5("p_5YearAverageDividendYield","Durchschn. Dividendenrendite(5 Jahre)",true),
	PAYOUTRATIO("payoutRatio","Auszahlungskurs",true);

	
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
