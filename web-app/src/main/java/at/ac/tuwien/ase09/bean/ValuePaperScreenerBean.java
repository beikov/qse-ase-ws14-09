package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.filter.AttributeType;
import at.ac.tuwien.ase09.filter.OperatorType;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.ValuePaperScreenerService;

@ManagedBean
@ViewScoped
public class ValuePaperScreenerBean implements Serializable {

	private static final long serialVersionUID = 1L;
		
		@Inject
		private ValuePaperScreenerService screenerService;
		

		private ValuePaperType paperType;

		private List<AttributeFilter> filters=new ArrayList<AttributeFilter>();
		private List<ValuePaper> searchedValuePapers;
		
		@PostConstruct
		public void init() {
		}
		
		public List<AttributeFilter> getFilters() {
			return filters;
		}

		public void setFilters(List<AttributeFilter> filters) {
			this.filters = filters;
		}

		public List<ValuePaper> getSearchedValuePapers() {
			return searchedValuePapers;
		}

		public void setSearchedValuePapers(List<ValuePaper> searchedValuePapers) {
			this.searchedValuePapers = searchedValuePapers;
		}

		public ValuePaperType[] getValuePaperTypes()
		{
			return ValuePaperType.values();
		}
		public OperatorType[] getOperatorTypes()
		{
			return OperatorType.values();
		}
		public List<AttributeType> getAttributeTypes()
		{
			
			if(paperType==null||paperType.equals(ValuePaperType.STOCK))
			{
				return Arrays.asList(AttributeType.values());
			}
			else
			{
				List<AttributeType> attributes=new ArrayList<AttributeType>();
				attributes.add(AttributeType.NAME);
				attributes.add(AttributeType.CODE);
				return attributes;
			}
		}
		
		public void setValuePaperType(ValuePaperType paperType)
		{
			this.paperType=paperType;
		}
		
		public ValuePaperType getValuePaperType()
		{
			return paperType;
		}

		public void delete(AttributeFilter filter)
		{
			filters.remove(filter);
		}
		public void addFilter()
		{
			System.out.println("Hallo");
			filters.add(new AttributeFilter());
			for(AttributeFilter f: filters)
			{
				System.out.println("Hallo1");
			}
		}
		public void search()
		{
			
			
				try{
					searchedValuePapers=screenerService.search(filters, paperType);
				}
				catch(IllegalArgumentException e)
				 {
					System.out.println("currencyCode does not exists");
					FacesMessage facesMessage = new FacesMessage(
							"Fehler: W�hrungs-Code existiert nicht");
					FacesContext.getCurrentInstance().addMessage(
							"searchValuePapers:currency", facesMessage);

				}
			
			
			
			
			for(ValuePaper p: searchedValuePapers)
			{
				System.out.println(p.toString());
			}
		}
		
		
		
		
}
