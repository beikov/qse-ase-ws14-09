package at.ac.tuwien.ase09.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Embeddable;

@Embeddable
public class PortfolioVisibility implements Serializable {

	private Boolean publicVisible = false;
	private Boolean statisticsVisible = false;
	private Boolean historyVisible = false;
	private Boolean valuePaperListVisible = false;
	
}
