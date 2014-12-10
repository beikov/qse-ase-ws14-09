package at.ac.tuwien.ase09.bean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class WatchBean {

	private String watchExpression = "(MARKETCAP < 9829 AND ENTERPRISEVALUE > SQRT(REVENUE*3) + 100) OR TOTALCASH < 0";

	public String getWatchExpression() {
		return watchExpression;
	}

	public void setWatchExpression(String watchExpression) {
		this.watchExpression = watchExpression;
	}
}
