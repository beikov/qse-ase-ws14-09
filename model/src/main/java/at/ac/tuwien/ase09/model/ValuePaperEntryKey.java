package at.ac.tuwien.ase09.model;

import javax.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ValuePaperEntryKey implements Serializable {

	// private StockMarketGame game;
	// private ValuePaper valuePaper;
	private Long gameId;
	private Long valuePaperId;

	public ValuePaperEntryKey() {
		super();
	}

	public ValuePaperEntryKey(Long gameId, Long valuePaperId) {
		super();
		this.gameId = gameId;
		this.valuePaperId = valuePaperId;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Long getValuePaperId() {
		return valuePaperId;
	}

	public void setValuePaperId(Long valuePaperId) {
		this.valuePaperId = valuePaperId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gameId == null) ? 0 : gameId.hashCode());
		result = prime * result
				+ ((valuePaperId == null) ? 0 : valuePaperId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValuePaperEntryKey other = (ValuePaperEntryKey) obj;
		if (gameId == null) {
			if (other.gameId != null)
				return false;
		} else if (!gameId.equals(other.gameId))
			return false;
		if (valuePaperId == null) {
			if (other.valuePaperId != null)
				return false;
		} else if (!valuePaperId.equals(other.valuePaperId))
			return false;
		return true;
	}

}
