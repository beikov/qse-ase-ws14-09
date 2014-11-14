package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.StockMarketGame;

@Entity
@DiscriminatorValue(NotificationType.TYPE_GAME_STARTED)
public class GameStartedNotification extends Notification {
	private static final long serialVersionUID = 1L;

	private StockMarketGame game;
	
	@Override
	@Transient
	public NotificationType getType() {
		return NotificationType.GAME_STARTED;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	public StockMarketGame getGame() {
		return game;
	}

	public void setGame(StockMarketGame game) {
		this.game = game;
	}
	
}
