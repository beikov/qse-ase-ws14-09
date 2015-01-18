package at.ac.tuwien.ase09.currency;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ac.tuwien.ase09.exception.AppException;

@Stateless
public class CurrencyConversionService {
	private static final String YAHOO_CURRENCY_CONVERSION_QUERY_TEMPLATE = "select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22#{conversion}%22)";
	private static final String YAHOO_CURRENCY_CONVERSION_ENDPOINT_URI = "https://query.yahooapis.com/v1/public/yql";

	/**
	 * 
	 * @param value Money value in the source currency
	 * @param source Currency for the value parameter
	 * @param target Target currency that the value should be converted to
	 * @return
	 */
	public BigDecimal getConversionRate(Currency source,
			Currency target) {
		final String sourceCode = source.getCurrencyCode();
		final String targetCode = target.getCurrencyCode();

		Client restClient = ClientBuilder.newClient();
		String finalQuery = YAHOO_CURRENCY_CONVERSION_QUERY_TEMPLATE
				.replaceAll("#\\{conversion\\}", sourceCode + targetCode);
		WebTarget webTarget = restClient
				.target(YAHOO_CURRENCY_CONVERSION_ENDPOINT_URI)
				.queryParam("q", finalQuery)
				.queryParam("env",
						"store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
		Response response = webTarget.request(MediaType.APPLICATION_JSON).get();
		if(response.getStatus() == Response.Status.OK.getStatusCode()){
			InputStream inputStream = response.readEntity(InputStream.class);
			JsonObject json = Json.createReader(inputStream).readObject();
			JsonObject rate = json.getJsonObject("query").getJsonObject("results").getJsonObject("rate");
			return new BigDecimal(rate.getJsonString("Rate").getString());
			
		}else{
			throw new AppException("Bad response code: " + response.getStatus());
		}
	}
	
	public static BigDecimal convert(BigDecimal value, BigDecimal conversionRate){
		return value.multiply(conversionRate, new MathContext(9, RoundingMode.HALF_DOWN));
	}

}
