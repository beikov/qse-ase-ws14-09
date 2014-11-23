package at.ac.tuwien.ase09.bean;

import java.util.Currency;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.service.ValuePaperScreenerService;

@ManagedBean
@Named
@RequestScoped
public class ValuePaperScreenerBean {

		
		@Inject
		private ValuePaperScreenerService screenerService;
		
		private ValuePaper valuePaper;
		private ValuePaperType paperType;
		private Boolean isTypeSpecificated=false;
		private String valuePaperName, code, country, currencyCode;
		private List<ValuePaper> searchedValuePapers;
		
		
		@PostConstruct
		public void init() {
			
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
		
		public void setValuePaperType(ValuePaperType paperType)
		{
			this.paperType=paperType;
		}
		
		public ValuePaperType getValuePaperType()
		{
			return paperType;
		}

		public String getValuePaperName() {
			return valuePaperName;
		}

		public void setValuePaperName(String valuePaperName) {
			this.valuePaperName = valuePaperName;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode=currencyCode;
		}
		
		public void search()
		{
			if(paperType!=null)
			{
				System.out.println(""+paperType.toString());
				isTypeSpecificated=true;
				
				if(paperType.equals(ValuePaperType.BOND))
				{
					valuePaper=new StockBond();
				}
				else if(paperType.equals(ValuePaperType.STOCK))
				{
					valuePaper=new Stock();
				}
				else
				{
					valuePaper=new Fund();
				}
				/**
				try {
					valuePaper=(ValuePaper) Class.forName(paperType.toString().charAt(0)+paperType.toString().substring(1).toLowerCase()).newInstance();
				} catch (InstantiationException | IllegalAccessException
						| ClassNotFoundException e) {
					e.printStackTrace();
				}*/
			}
			else
			{
				isTypeSpecificated=false;
				valuePaper=new Stock();
			}
			if (valuePaper.getType() == ValuePaperType.STOCK) {
				((Stock)valuePaper).setCountry(country);
			}
			valuePaper.setCode(code);
			valuePaper.setName(valuePaperName);
			
			if (valuePaper.getType() == ValuePaperType.STOCK && !currencyCode.isEmpty() && currencyCode != null) {
				try{
					((Stock)valuePaper).setCurrency(Currency.getInstance(currencyCode));
				}
				catch(IllegalArgumentException e)
				 {
					System.out.println("currencyCode does not exists");
					FacesMessage facesMessage = new FacesMessage(
							"Error: Currency-Code does not exist");
					FacesContext.getCurrentInstance().addMessage(
							"searchValuePapers:currency", facesMessage);

				}
			}
			
			searchedValuePapers=screenerService.search(valuePaper, isTypeSpecificated);
			
			for(ValuePaper p: searchedValuePapers)
			{
				System.out.println(p.toString());
			}
		}
		
		
		
		
}
