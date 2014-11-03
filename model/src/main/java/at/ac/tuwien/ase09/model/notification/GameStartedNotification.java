package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.StockMarketGame;

@Entity
@DiscriminatorValue(NotificationType.TYPE_GAME_STARTED)
public class GameStartedNotification extends Notification {

	private StockMarketGame game;
	
	@Override
	public NotificationType getType() {
		return NotificationType.GAME_STARTED;
	}
}
