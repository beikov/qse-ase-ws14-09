package at.ac.tuwien.ase09.context;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named
public class PortfolioContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long contextId;

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
}
