package at.ac.tuwien.ase09.test.currency;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import at.ac.tuwien.ase09.currency.CurrencyConversionService;

@Stateless
@Specializes
public class TestCurrencyConversionService extends CurrencyConversionService {
	
	@Inject
	private CurrencyConversionHolder currencyConversionHolder;
	
	@Override
	public BigDecimal getConversionRate(Currency source, Currency target) {
		Map<Currency, Map<Currency, BigDecimal>> conversionRates = currencyConversionHolder.getConversionRates();
		if(source.equals(target)){
			return new BigDecimal(1);
		}else{
			return conversionRates.get(source).get(target);
		}
	}
}
