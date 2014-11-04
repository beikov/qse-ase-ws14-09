package at.ac.tuwien.ase09.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PortfolioVisibility implements Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean publicVisible = false;
	private Boolean statisticsVisible = false;
	private Boolean historyVisible = false;
	private Boolean valuePaperListVisible = false;
	
	public Boolean getPublicVisible() {
		return publicVisible;
	}
	
	public void setPublicVisible(Boolean publicVisible) {
		this.publicVisible = publicVisible;
	}
	
	public Boolean getStatisticsVisible() {
		return statisticsVisible;
	}
	
	public void setStatisticsVisible(Boolean statisticsVisible) {
		this.statisticsVisible = statisticsVisible;
	}
	
	public Boolean getHistoryVisible() {
		return historyVisible;
	}
	
	public void setHistoryVisible(Boolean historyVisible) {
		this.historyVisible = historyVisible;
	}
	
	public Boolean getValuePaperListVisible() {
		return valuePaperListVisible;
	}
	
	public void setValuePaperListVisible(Boolean valuePaperListVisible) {
		this.valuePaperListVisible = valuePaperListVisible;
	}
	
}
