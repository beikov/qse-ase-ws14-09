package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import at.ac.tuwien.ase09.data.WatchDataAccess;
import at.ac.tuwien.ase09.model.EntityDataModel;
import at.ac.tuwien.ase09.model.FilterModel;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.Watch;
import at.ac.tuwien.ase09.model.filter.Attribute;
import at.ac.tuwien.ase09.model.filter.AttributeFilter;
import at.ac.tuwien.ase09.parser.PWatchCompiler;
import at.ac.tuwien.ase09.service.WatchService;
import at.ac.tuwien.ase09.utils.BeanUtils;
import at.ac.tuwien.ase09.validator.PWatchExpressionValidator;

@Named
@ViewScoped
public class WatchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private WatchDataAccess watchDataAccess;
	@Inject
	private WatchService watchService;

	private Stock stock;
	
	private FilterModel filterModel;
	private Watch selectedWatch;
	private String watchExpression;
	private String activeIndex;
	private boolean advancedOnly;
	
	@PostConstruct
	private void init() {
		filterModel = new FilterModel();
		filterModel.setValuePaperType(ValuePaperType.STOCK);
		filterModel.addExcludedAttributes(
				Attribute.NAME,
				Attribute.CODE,
				Attribute.INDEX,
				Attribute.COUNTRY,
				Attribute.CURRENCY
		);
		
		activeIndex = "0";
		newWatch();
	}
	
	public void newWatch() {
		setSelectedWatch(new Watch());
	}
	
	public void saveWatch() {
		if ("0".equals(activeIndex)) {
			selectedWatch.setExpression(PWatchCompiler.attributeFiltersAsPWatch(filterModel.getFilters()));
		} else {
			selectedWatch.setExpression(watchExpression);
		}
		
		if (selectedWatch.getId() == null) {
			selectedWatch = watchService.addWatch(selectedWatch);
		} else {
			selectedWatch = watchService.updateWatch(selectedWatch);
		}

		BeanUtils.removeInstance("watches");
	}
	
	public void removeWatch() {
		watchService.removeWatch(selectedWatch);
		newWatch();
		BeanUtils.removeInstance("watches");
	}

	public void setSelectedWatch(Watch selectedWatch) {
		if (selectedWatch == null) {
			selectedWatch = new Watch();
		}
		
		this.selectedWatch = selectedWatch;
		setWatchExpression(selectedWatch.getExpression());
	}
		
	public void setWatchExpression(String watchExpression) {
		if (watchExpression == null) {
			watchExpression = "";
		}
		
		this.watchExpression = watchExpression;
		List<AttributeFilter> filters = PWatchCompiler.pwatchAsAttributeFilters(watchExpression, false, ValuePaperType.STOCK);
		
		if (filters == null) {
			filterModel.setFilters(new ArrayList<>());
			advancedOnly = true;
			activeIndex = "1";
		} else {
			filterModel.setFilters(filters);
			advancedOnly = false;
		}
	}
	
	@Named("watches")
	@RequestScoped
	@Produces
	EntityDataModel<Watch> getWatches() {
		return new EntityDataModel<Watch>(watchDataAccess.getWatches());
	}
	
	@Named("watchValidator")
	@RequestScoped
	@Produces
	PWatchExpressionValidator getWatchValidator() {
		return new PWatchExpressionValidator("0".equals(activeIndex), false, ValuePaperType.STOCK);
	}
	
	// Getters + Setters

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public FilterModel getFilterModel() {
		return filterModel;
	}

	public Watch getSelectedWatch() {
		return selectedWatch;
	}

	public String getWatchExpression() {
		return watchExpression;
	}

	public boolean isAdvancedOnly() {
		return advancedOnly;
	}

	public String getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(String activeIndex) {
		this.activeIndex = activeIndex;
	}
}
