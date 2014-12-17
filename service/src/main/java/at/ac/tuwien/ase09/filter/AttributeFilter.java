package at.ac.tuwien.ase09.filter;

import java.math.BigDecimal;

import at.ac.tuwien.ase09.model.ValuePaperType;

public class AttributeFilter {

	private BigDecimal numericValue;
	private String textValue;
	private String currencyValue;
	private String indexValue;
	private AttributeType attribute;
	private OperatorType operator;
	private Boolean numeric,enabled;
	
	
	public AttributeFilter()
	{
		enabled=true;
	}
	public String getCurrencyValue() {
		return currencyValue;
	}
	public void setCurrencyValue(String currencyValue) {
		this.currencyValue = currencyValue;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public BigDecimal getNumericValue() {
		return numericValue;
	}
	public void setNumericValue(BigDecimal numericValue) {
		this.numericValue = numericValue;
	}
	public String getTextValue() {
		return textValue;
	}
	
	public String getIndexValue() {
		return indexValue;
	}
	public void setIndexValue(String indexValue) {
		this.indexValue = indexValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public AttributeType getAttribute() {
		return attribute;
	}
	public void setAttribute(AttributeType attribute) {
		this.attribute = attribute;
	}
	public OperatorType getOperator() {
		return operator;
	}
	public void setOperator(OperatorType operator) {
		this.operator = operator;
	}
	public Boolean getNumeric() {
		return attribute!=null ? attribute.isNumeric():false;
	}
	public void setNumeric(Boolean numeric) {
		this.numeric = numeric;
	}
	
	
	
}
