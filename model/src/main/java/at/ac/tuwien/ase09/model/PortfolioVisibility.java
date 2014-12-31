package at.ac.tuwien.ase09.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PortfolioVisibility implements Serializable {
	private static final long serialVersionUID = 1L;

	private Boolean publicVisible = false;
	private Boolean statisticsVisible = false;
	private Boolean valuePaperListVisible = false;
	private Boolean chartsVisible = false;
	private Boolean orderHistoryVisible = false;
	private Boolean transactionHistoryVisible = false;
	private Boolean newsVisible = false;
	private Boolean analystOpinionsVisible = false;
	
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
	
	public Boolean getOrderHistoryVisible() {
		return orderHistoryVisible;
	}

	public void setOrderHistoryVisible(Boolean orderHistoryVisible) {
		this.orderHistoryVisible = orderHistoryVisible;
	}

	public Boolean getTransactionHistoryVisible() {
		return transactionHistoryVisible;
	}

	public void setTransactionHistoryVisible(Boolean transactionHistoryVisible) {
		this.transactionHistoryVisible = transactionHistoryVisible;
	}

	public Boolean getValuePaperListVisible() {
		return valuePaperListVisible;
	}
	
	public void setValuePaperListVisible(Boolean valuePaperListVisible) {
		this.valuePaperListVisible = valuePaperListVisible;
	}

	public Boolean getChartsVisible() {
		return chartsVisible;
	}

	public void setChartsVisible(Boolean chartsVisible) {
		this.chartsVisible = chartsVisible;
	}

	public Boolean getNewsVisible() {
		return newsVisible;
	}

	public void setNewsVisible(Boolean newsVisible) {
		this.newsVisible = newsVisible;
	}

	public Boolean getAnalystOpinionsVisible() {
		return analystOpinionsVisible;
	}

	public void setAnalystOpinionsVisible(Boolean analystOpinionsVisible) {
		this.analystOpinionsVisible = analystOpinionsVisible;
	}
	
}
