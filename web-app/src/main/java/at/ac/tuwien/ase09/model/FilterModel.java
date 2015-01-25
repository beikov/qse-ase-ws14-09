package at.ac.tuwien.ase09.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.ase09.model.filter.Attribute;
import at.ac.tuwien.ase09.model.filter.AttributeFilter;

public class FilterModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final List<Attribute> STOCK_ATTRIBUTES;
	private static final List<Attribute> FUND_ATTRIBUTES;
	private static final List<Attribute> BOND_ATTRIBUTES;

	private List<Attribute> attributes;
	private List<AttributeFilter> filters = new ArrayList<AttributeFilter>();
	private Set<Attribute> excludedAttributes = new HashSet<>();
	private ValuePaperType valuePaperType;
	
	static {
		STOCK_ATTRIBUTES = Arrays.asList(Attribute.values());
		FUND_ATTRIBUTES = Arrays.asList(
				Attribute.NAME,
				Attribute.CODE,
				Attribute.CURRENCY);
		BOND_ATTRIBUTES = Arrays.asList(
				Attribute.NAME,
				Attribute.CODE
				);
	}
	
	public FilterModel() {
		updateAttributeList();
	}

	public void delete(AttributeFilter filter) {
		filters.remove(filter);
	}

	public void addFilter() {
		filters.add(new AttributeFilter());
	}
	
	public void addExcludedAttributes(Attribute... attributes) {
		excludedAttributes.addAll(Arrays.asList(attributes));
		updateAttributeList();
	}

	public void setValuePaperType(ValuePaperType paperType) {
		this.valuePaperType = paperType;
		updateAttributeList();

		// Remove attributes that are not valid any more
		Iterator<AttributeFilter> iter = filters.iterator();
		while (iter.hasNext()) {
			AttributeFilter filter = iter.next();
			
			if (!attributes.contains(filter.getAttribute())) {
				iter.remove();
			}
		}
	}
	
	private void updateAttributeList() {
		if (valuePaperType == null || valuePaperType == ValuePaperType.STOCK) {
			attributes = STOCK_ATTRIBUTES;
		} else if (valuePaperType == ValuePaperType.FUND) {
			attributes = FUND_ATTRIBUTES;
		} else {
			attributes = BOND_ATTRIBUTES;
		}
		
		if (!excludedAttributes.isEmpty()) {
			attributes = new ArrayList<>(attributes);
			attributes.removeAll(excludedAttributes);
		}
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setFilters(List<AttributeFilter> filters) {
		this.filters = filters;
	}

	public List<AttributeFilter> getFilters() {
		return filters;
	}

	public ValuePaperType getValuePaperType() {
		return valuePaperType;
	}
}
