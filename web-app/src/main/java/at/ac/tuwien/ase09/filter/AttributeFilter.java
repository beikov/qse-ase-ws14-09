package at.ac.tuwien.ase09.filter;

import java.math.BigDecimal;

import at.ac.tuwien.ase09.model.ValuePaperType;

public class AttributeFilter {

	private BigDecimal numericValue;
	private String textValue;
	private ValuePaperType typeValue;
	private AttributeType attribute;
	private OperatorType operator;
	private Boolean numeric,enabled;
	
	
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
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public ValuePaperType getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(ValuePaperType typeValue) {
		this.typeValue = typeValue;
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
	public Boolean isNumeric() {
		return attribute.isNumeric();
	}
	public void setNumeric(Boolean numeric) {
		this.numeric = numeric;
	}
	
	
	
}
