package at.ac.tuwien.ase09.model.filter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AttributeFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Attribute attribute;
	private OperatorType operatorType;
	private BigDecimal numericValue;
	private Date dateTimeValue;
	private String currencyValue;
	private String indexValue;
	private String textValue;
	private boolean enabled = true;
	
	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
		if (!getAllowedOperatorTypes().contains(operatorType)) {
			operatorType = null;
		}
		
		numericValue = null;
		dateTimeValue = null;
		currencyValue = null;
		indexValue = null;
		textValue = null;
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public BigDecimal getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(BigDecimal numericValue) {
		this.numericValue = numericValue;
	}

	public Date getDateTimeValue() {
		return dateTimeValue;
	}

	public void setDateTimeValue(Date dateTimeValue) {
		this.dateTimeValue = dateTimeValue;
	}

	public String getCurrencyValue() {
		return currencyValue;
	}

	public void setCurrencyValue(String currencyValue) {
		this.currencyValue = currencyValue;
	}

	public String getIndexValue() {
		return indexValue;
	}

	public void setIndexValue(String indexValue) {
		this.indexValue = indexValue;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<OperatorType> getAllowedOperatorTypes() {
		if (attribute == null) {
			return Collections.emptyList();
		}
		
		return attribute.getAttributeType().getAllowedOperatorTypes();
	}

	public boolean isNumericType() {
		return attribute != null && attribute.getAttributeType() == AttributeType.NUMERIC;
	}

	public boolean isDateTimeType() {
		return attribute != null && attribute.getAttributeType() == AttributeType.DATE_TIME;
	}

	public boolean isStringType() {
		return attribute != null && attribute.getAttributeType() == AttributeType.STRING;
	}

	public boolean isCurrencyType() {
		return attribute == Attribute.CURRENCY;
	}

	public boolean isIndexType() {
		return attribute == Attribute.INDEX;
	}

	public boolean isTextType() {
		return isStringType() && !isCurrencyType() && !isIndexType();
	}

}
