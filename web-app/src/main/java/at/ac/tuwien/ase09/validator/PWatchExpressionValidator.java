package at.ac.tuwien.ase09.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.parser.PWatchCompiler;

public class PWatchExpressionValidator implements Validator {
	
	private final boolean simple;
	private final boolean stringExpressionAllowed;
	private final ValuePaperType valuePaperType;

	/**
	 * Constructor for proxies
	 */
	public PWatchExpressionValidator() {
		this.simple = false;
		this.stringExpressionAllowed = false;
		this.valuePaperType = null;
	}

	public PWatchExpressionValidator(boolean simple, boolean stringExpressionAllowed, ValuePaperType valuePaperType) {
		this.simple = simple;
		this.stringExpressionAllowed = stringExpressionAllowed;
		this.valuePaperType = valuePaperType;
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value == null) {
			return;
		}
		
		String error = PWatchCompiler.validate((String) value, simple, stringExpressionAllowed, valuePaperType);
		
		if (error != null) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, error, ""));
		}
	}

	public boolean isSimple() {
		return simple;
	}

	public boolean isStringExpressionAllowed() {
		return stringExpressionAllowed;
	}

	public ValuePaperType getValuePaperType() {
		return valuePaperType;
	}

}
