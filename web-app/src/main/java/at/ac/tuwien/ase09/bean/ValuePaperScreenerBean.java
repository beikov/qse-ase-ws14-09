package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.model.FilterModel;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.validator.PWatchExpressionValidator;

@Named
@ViewScoped
public class ValuePaperScreenerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperScreenerAccess screenerDataAccess;

	private FilterModel filterModel;

	private ValuePaperType valuePaperType;
	private String expression;
	private String activeIndex;

	private List<ValuePaper> searchedValuePapers;

	@PostConstruct
	private void init() {
		filterModel = new FilterModel();
		activeIndex = "0";
	}

	public ValuePaperType[] getValuePaperTypes() {
		return ValuePaperType.values();
	}

	@Named("usedCurrencies")
	@RequestScoped
	@Produces
	List<Currency> getUsedCurrencies() {
		return screenerDataAccess.getUsedCurrencyCodes();
	}

	@Named("usedIndices")
	@RequestScoped
	@Produces
	List<String> getUsedIndexes() {
		return screenerDataAccess.getUsedIndexes();
	}
	
	@Named("screenerValidator")
	@RequestScoped
	@Produces
	PWatchExpressionValidator getWatchValidator() {
		return new PWatchExpressionValidator("0".equals(activeIndex), true, valuePaperType);
	}

	public void setValuePaperType(ValuePaperType paperType) {
		this.valuePaperType = paperType;
		filterModel.setValuePaperType(valuePaperType);
	}

	public void search() {
		if ("0".equals(activeIndex)) {
			searchedValuePapers = screenerDataAccess.findByFilter(filterModel.getFilters(), filterModel.getValuePaperType());
		} else {
			searchedValuePapers = screenerDataAccess.findByExpression(expression, valuePaperType);
		}
	}

	// Getters + Setters

	public FilterModel getFilterModel() {
		return filterModel;
	}

	public ValuePaperType getValuePaperType() {
		return valuePaperType;
	}

	public List<ValuePaper> getSearchedValuePapers() {
		return searchedValuePapers;
	}

	public void setSearchedValuePapers(List<ValuePaper> searchedValuePapers) {
		this.searchedValuePapers = searchedValuePapers;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(String activeIndex) {
		this.activeIndex = activeIndex;
	}

}
