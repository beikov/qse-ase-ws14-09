package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.filter.AttributeType;
import at.ac.tuwien.ase09.filter.OperatorType;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperScreenerAccessTest extends AbstractContainerTest<ValuePaperScreenerAccessTest>{

	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ValuePaperScreenerAccess valuePaperScreener;
	
	@Before
	public void  init()
	{
		
		//Stocks
		Stock stockAtx = new Stock();
		stockAtx.setCode("stock1");
		stockAtx.setIndex("ATX");
		stockAtx.setCountry("Österreich");
		stockAtx.setCurrency(Currency.getInstance("EUR"));
		stockAtx.setName("Andritz");
		
		stockAtx.setMarketCap(new BigDecimal(21000000));
		stockAtx.setEnterpriseValue(new BigDecimal(750000000));
		stockAtx.setPriceSales(new BigDecimal(2));
		stockAtx.setPriceBook(new BigDecimal(1.5));
		stockAtx.setEnterpriseValueRevenue(new BigDecimal(26.55));
		stockAtx.setEnterpriseValueEBITDA(new BigDecimal(34.67));
		stockAtx.setProfitMargin(new BigDecimal(300000));
		stockAtx.setReturnonAssets(new BigDecimal(66));
		stockAtx.setReturnonEquity(new BigDecimal(43));
		stockAtx.setRevenue(new BigDecimal(1200000));
		stockAtx.setGrossProfit(new BigDecimal(420000));
		stockAtx.setEbitda(new BigDecimal(390000));
		stockAtx.setTotalCash(new BigDecimal(550000));
		stockAtx.setTotalCashPerShare(new BigDecimal(1.233));
		stockAtx.setTotalDebt(new BigDecimal(45000000));
		stockAtx.setTotalDebtEquity(new BigDecimal(1.3));
		stockAtx.setOperatingCashFlow(new BigDecimal(340000));
		stockAtx.setBeta(new BigDecimal(0.6));
		stockAtx.setP_52_WeekChange(new BigDecimal(0.67));
		stockAtx.setP_52_WeekHigh(new BigDecimal(60.5));
		stockAtx.setP_52_WeekLow(new BigDecimal(55.45));
		stockAtx.setAvgVol_3month(new BigDecimal(160000));
		stockAtx.setAvgVol_10day(new BigDecimal(45000));
		stockAtx.setSharesOutstanding(new BigDecimal(96000));
		stockAtx.setFloatVal(new BigDecimal(23000));
		stockAtx.setPercentageHeldbyInsiders(new BigDecimal(43));
		stockAtx.setPercentageHeldbyInstitutions(new BigDecimal(57));
		stockAtx.setSharesShortCurrentMonth(new BigDecimal(12000));
		stockAtx.setP_5YearAverageDividendYield(new BigDecimal(50.5));
		stockAtx.setPayoutRatio(new BigDecimal(51.3));
		dataManager.persist(stockAtx);
		
		Stock stockNasdaq = new Stock();
		stockNasdaq.setCode("stock2");
		stockNasdaq.setIndex("Nasdaq100");
		
		stockNasdaq.setCountry("USA");
		stockNasdaq.setCurrency(Currency.getInstance("USD"));
		stockNasdaq.setName("Google");
		
		stockNasdaq.setMarketCap(new BigDecimal(71000000));
		stockNasdaq.setEnterpriseValue(new BigDecimal(1500000000));
		stockNasdaq.setPriceSales(new BigDecimal(3));
		stockNasdaq.setPriceBook(new BigDecimal(2));
		stockNasdaq.setEnterpriseValueRevenue(new BigDecimal(46.356));
		stockNasdaq.setEnterpriseValueEBITDA(new BigDecimal(64.17));
		stockNasdaq.setProfitMargin(new BigDecimal(800000));
		stockNasdaq.setReturnonAssets(new BigDecimal(73));
		stockNasdaq.setReturnonEquity(new BigDecimal(51));
		stockNasdaq.setRevenue(new BigDecimal(2200000));
		stockNasdaq.setGrossProfit(new BigDecimal(820000));
		stockNasdaq.setEbitda(new BigDecimal(590000));
		stockNasdaq.setTotalCash(new BigDecimal(770000));
		stockNasdaq.setTotalCashPerShare(new BigDecimal(2.212));
		stockNasdaq.setTotalDebt(new BigDecimal(95000000));
		stockNasdaq.setTotalDebtEquity(new BigDecimal(1.2));
		stockNasdaq.setOperatingCashFlow(new BigDecimal(740000));
		stockNasdaq.setBeta(new BigDecimal(0.5));
		stockNasdaq.setP_52_WeekChange(new BigDecimal(2.67));
		stockNasdaq.setP_52_WeekHigh(new BigDecimal(150.5));
		stockNasdaq.setP_52_WeekLow(new BigDecimal(133.45));
		stockNasdaq.setAvgVol_3month(new BigDecimal(760000));
		stockNasdaq.setAvgVol_10day(new BigDecimal(95000));
		stockNasdaq.setSharesOutstanding(new BigDecimal(186000));
		stockNasdaq.setFloatVal(new BigDecimal(29000));
		stockNasdaq.setPercentageHeldbyInsiders(new BigDecimal(61));
		stockNasdaq.setPercentageHeldbyInstitutions(new BigDecimal(39));
		stockNasdaq.setSharesShortCurrentMonth(new BigDecimal(55000));
		stockNasdaq.setP_5YearAverageDividendYield(new BigDecimal(61.5));
		stockNasdaq.setPayoutRatio(new BigDecimal(45.3));
		dataManager.persist(stockNasdaq);
		
		//Bonds
		StockBond stockBondAtx1 = new StockBond();
		stockBondAtx1.setCode("bond1");
		stockBondAtx1.setName("Bond1");
		StockBond stockBondAtx2 = new StockBond();
		stockBondAtx2.setCode("bond2");
		stockBondAtx1.setName("Bond2");
		StockBond stockBondNasdaq1 = new StockBond();
		stockBondNasdaq1.setCode("bond3");
		stockBondAtx1.setName("Bond3");
			
		stockBondAtx1.setBaseStock(stockAtx);
		stockBondAtx2.setBaseStock(stockAtx);
		stockBondNasdaq1.setBaseStock(stockNasdaq);
		
		dataManager.persist(stockBondAtx1);
		dataManager.persist(stockBondAtx2);
		dataManager.persist(stockBondNasdaq1);
		
		//Fund
		Fund f = new Fund();
		f.setCode("fund1");
		f.setName("Superfund");
		dataManager.persist(f);
		em.clear();
	}
	
	 
	@Test
	public void testDefaultBehavior()
	{
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(null, null);
		
		assertEquals(6,valuePapers.size());
	}
	
	@Test
	public void testTypeFilterStock()
	{
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(null, ValuePaperType.STOCK);
		
		assertEquals(2,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertEquals(ValuePaperType.STOCK,vp.getType());
		}
	}
	@Test
	public void testTypeFilterBond()
	{
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(null, ValuePaperType.BOND);
		
		assertEquals(3,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertEquals(ValuePaperType.BOND,vp.getType());
		}
	}
	@Test
	public void testTypeFilterFund()
	{
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(null, ValuePaperType.FUND);
		
		assertEquals(1,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertEquals(ValuePaperType.FUND,vp.getType());
		}
	}
	@Test
	public void testNameFilter()
	{
		AttributeFilter atfilter=new AttributeFilter();
		atfilter.setAttribute(AttributeType.NAME);
		atfilter.setTextValue("Andritz");
		List<AttributeFilter> filterList=new ArrayList<AttributeFilter>();
		filterList.add(atfilter);
		
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(filterList, null);
		
		assertEquals(1,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertEquals("Andritz",vp.getName());
		}
	}
	@Test
	public void testCurrencyFilter()
	{
		AttributeFilter atfilter=new AttributeFilter();
		atfilter.setAttribute(AttributeType.CURRENCY);
		atfilter.setCurrencyValue("EUR");
		List<AttributeFilter> filterList=new ArrayList<AttributeFilter>();
		filterList.add(atfilter);
		
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(filterList, ValuePaperType.STOCK);
		
		assertEquals(1,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertEquals("EUR",((Stock)vp).getCurrency().getCurrencyCode());
		}
	}
	@Test
	public void testGetCurrenciesAvailable()
	{
		
		List<Currency> currencies=valuePaperScreener.getUsedCurrencyCodes();
		
		assertEquals(2,currencies.size());
		
	}
	@Test
	public void testCountryFilter_Wildcards()
	{
		Stock stockAtx = new Stock();
		stockAtx.setCode("stock3");
		stockAtx.setIndex("ATX");
		stockAtx.setCountry("Österrund");
		
		Stock stockAtx2 = new Stock();
		stockAtx2.setCode("stock4");
		stockAtx2.setIndex("ATX");
		stockAtx2.setCountry("Österresch");
		dataManager.persist(stockAtx);
		dataManager.persist(stockAtx2);
		em.clear();
		
		AttributeFilter atfilter=new AttributeFilter();
		atfilter.setAttribute(AttributeType.COUNTRY);
		atfilter.setTextValue("Österre?ch");
		List<AttributeFilter> filterList=new ArrayList<AttributeFilter>();
		filterList.add(atfilter);
		
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(filterList, ValuePaperType.STOCK);
		
		assertEquals(2,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertTrue(((Stock)vp).getCountry().matches("Österre.ch"));
		}
		
		AttributeFilter atfilter2=new AttributeFilter();
		atfilter2.setAttribute(AttributeType.COUNTRY);
		atfilter2.setTextValue("Öster*");
		
		filterList.clear();
		filterList.add(atfilter2);
		
		valuePapers=valuePaperScreener.findByFilter(filterList, ValuePaperType.STOCK);
		
		assertEquals(3,valuePapers.size());
		for(ValuePaper vp: valuePapers)
		{
			assertTrue(((Stock)vp).getCountry().matches("Öster[a-zA-Z]*"));
		}
	}
	@Test
	public void testSpecificAttributesFilter()
	{
		List<AttributeFilter> filterList=new ArrayList<AttributeFilter>();
		
		AttributeFilter atfilter=new AttributeFilter();
		atfilter.setAttribute(AttributeType.MARKETCAP);
		atfilter.setNumericValue(new BigDecimal(71000000));
		atfilter.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter1=new AttributeFilter();
		atfilter1.setAttribute(AttributeType.ENTERPRISEVALUE);
		atfilter1.setNumericValue(new BigDecimal(1500000000));
		atfilter1.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter2=new AttributeFilter();
		atfilter2.setAttribute(AttributeType.PRICESALES);
		atfilter2.setNumericValue(new BigDecimal(3));
		atfilter2.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter3=new AttributeFilter();
		atfilter3.setAttribute(AttributeType.PRICEBOOK);
		atfilter3.setNumericValue(new BigDecimal(2));
		atfilter3.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter4=new AttributeFilter();
		atfilter4.setAttribute(AttributeType.ENTERPRISEVALUEREVENUE);
		atfilter4.setNumericValue(new BigDecimal(46.356));
		atfilter4.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter5=new AttributeFilter();
		atfilter5.setAttribute(AttributeType.ENTERPRISEVALUEEBITDA);
		atfilter5.setNumericValue(new BigDecimal(64.17));
		atfilter5.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter6=new AttributeFilter();
		atfilter6.setAttribute(AttributeType.PROFITMARGIN);
		atfilter6.setNumericValue(new BigDecimal(800000));
		atfilter6.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter7=new AttributeFilter();
		atfilter7.setAttribute(AttributeType.RETURNONASSETS);
		atfilter7.setNumericValue(new BigDecimal(73));
		atfilter7.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter8=new AttributeFilter();
		atfilter8.setAttribute(AttributeType.RETURNONEQUITY);
		atfilter8.setNumericValue(new BigDecimal(51));
		atfilter8.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter9=new AttributeFilter();
		atfilter9.setAttribute(AttributeType.REVENUE);
		atfilter9.setNumericValue(new BigDecimal(2200000));
		atfilter9.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter10=new AttributeFilter();
		atfilter10.setAttribute(AttributeType.GROSSPROFIT);
		atfilter10.setNumericValue(new BigDecimal(820000));
		atfilter10.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter11=new AttributeFilter();
		atfilter11.setAttribute(AttributeType.EBITDA);
		atfilter11.setNumericValue(new BigDecimal(590000));
		atfilter11.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter12=new AttributeFilter();
		atfilter12.setAttribute(AttributeType.TOTALCASH);
		atfilter12.setNumericValue(new BigDecimal(770000));
		atfilter12.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter13=new AttributeFilter();
		atfilter13.setAttribute(AttributeType.TOTALCASHSHARE);
		atfilter13.setNumericValue(new BigDecimal(2.212));
		atfilter13.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter14=new AttributeFilter();
		atfilter14.setAttribute(AttributeType.TOTALDEBT);
		atfilter14.setNumericValue(new BigDecimal(95000000));
		atfilter14.setOperator(OperatorType.EQUAL);	
		
		AttributeFilter atfilter15=new AttributeFilter();
		atfilter15.setAttribute(AttributeType.TOTALDEBTEQUITY);
		atfilter15.setNumericValue(new BigDecimal(1.2));
		atfilter15.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter16=new AttributeFilter();
		atfilter16.setAttribute(AttributeType.OPERATINGCASHFLOW);
		atfilter16.setNumericValue(new BigDecimal(740000));
		atfilter16.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter17=new AttributeFilter();
		atfilter17.setAttribute(AttributeType.BETA);
		atfilter17.setNumericValue(new BigDecimal(0.5));
		atfilter17.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter18=new AttributeFilter();
		atfilter18.setAttribute(AttributeType.WEEKCHANGE);
		atfilter18.setNumericValue(new BigDecimal(2.67));
		atfilter18.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter19=new AttributeFilter();
		atfilter19.setAttribute(AttributeType.WEEKHIGH);
		atfilter19.setNumericValue(new BigDecimal(150.5));
		atfilter19.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter20=new AttributeFilter();
		atfilter20.setAttribute(AttributeType.WEEKLOW);
		atfilter20.setNumericValue(new BigDecimal(133.45));
		atfilter20.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter21=new AttributeFilter();
		atfilter21.setAttribute(AttributeType.AVGVOLUME3M);
		atfilter21.setNumericValue(new BigDecimal(760000));
		atfilter21.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter22=new AttributeFilter();
		atfilter22.setAttribute(AttributeType.AVGVOLUME10D);
		atfilter22.setNumericValue(new BigDecimal(95000));
		atfilter22.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter23=new AttributeFilter();
		atfilter23.setAttribute(AttributeType.SHARESOUTSTANDING);
		atfilter23.setNumericValue(new BigDecimal(186000));
		atfilter23.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter24=new AttributeFilter();
		atfilter24.setAttribute(AttributeType.FLOATVALUE);
		atfilter24.setNumericValue(new BigDecimal(29000));
		atfilter24.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter25=new AttributeFilter();
		atfilter25.setAttribute(AttributeType.PERCINSIDERS);
		atfilter25.setNumericValue(new BigDecimal(61));
		atfilter25.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter26=new AttributeFilter();
		atfilter26.setAttribute(AttributeType.PERCINSTIT);
		atfilter26.setNumericValue(new BigDecimal(39));
		atfilter26.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter27=new AttributeFilter();
		atfilter27.setAttribute(AttributeType.SHARESSHORT);
		atfilter27.setNumericValue(new BigDecimal(55000));
		atfilter27.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter28=new AttributeFilter();
		atfilter28.setAttribute(AttributeType.DIVIDENDYIELD5);
		atfilter28.setNumericValue(new BigDecimal(61.5));
		atfilter28.setOperator(OperatorType.EQUAL);
		
		AttributeFilter atfilter29=new AttributeFilter();
		atfilter29.setAttribute(AttributeType.PAYOUTRATIO);
		atfilter29.setNumericValue(new BigDecimal(45.3));
		atfilter29.setOperator(OperatorType.EQUAL);
		
		filterList.add(atfilter);
		filterList.add(atfilter1);		
		filterList.add(atfilter2);	
		filterList.add(atfilter3);
		filterList.add(atfilter4);		
		filterList.add(atfilter5);	
		filterList.add(atfilter6);
		filterList.add(atfilter7);
		filterList.add(atfilter8);
		filterList.add(atfilter9);	
		filterList.add(atfilter10);
		filterList.add(atfilter11);
		filterList.add(atfilter12);	
		filterList.add(atfilter13);
		filterList.add(atfilter14);	
		filterList.add(atfilter15);
		filterList.add(atfilter16);
		filterList.add(atfilter17);
		filterList.add(atfilter18);
		filterList.add(atfilter19);
		filterList.add(atfilter20);
		filterList.add(atfilter21);
		filterList.add(atfilter22);
		filterList.add(atfilter23);
		filterList.add(atfilter24);
		filterList.add(atfilter25);
		filterList.add(atfilter26);
		filterList.add(atfilter27);
		filterList.add(atfilter28);
		filterList.add(atfilter29);
		
		
		
		List<ValuePaper> valuePapers=valuePaperScreener.findByFilter(filterList, ValuePaperType.STOCK);
		
		assertEquals(1,valuePapers.size());
		
		
	}
	

}
