package at.ac.tuwien.ase09.test.currency;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;

@Singleton
public class CurrencyConversionHolder {
	private Map<Currency, Map<Currency, BigDecimal>> conversionRates = new ConcurrentHashMap<>();
	
	public void addConversion(Currency source, Currency target, BigDecimal conversionRate){
		Map<Currency, BigDecimal> targetConversionRates = conversionRates.get(source);
		if(targetConversionRates == null){
			targetConversionRates = new HashMap<>();
			conversionRates.put(source, targetConversionRates);
		}
		targetConversionRates.put(target, conversionRate);
	}
	
	public Map<Currency, Map<Currency, BigDecimal>> getConversionRates(){
		return Collections.unmodifiableMap(new HashMap<Currency, Map<Currency,BigDecimal>>(conversionRates));
	}
}
