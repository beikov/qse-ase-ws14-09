package at.ac.tuwien.ase09.cep;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import com.espertech.esper.client.EPServiceProvider;

import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.service.OrderService;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class OrderExpirationSingleton {
	
    @Resource
    private TimerService timerService;
	@Inject
	private OrderService orderService;
	@Inject
	private EPServiceProvider epService;
	
	public Timer scheduleExpiration(Order order, String... statementNames) {
		Serializable info = new Object[] { order.getId(), statementNames };
		return timerService.createSingleActionTimer(order.getValidTo().getTime(), new TimerConfig(info, true));
	}

	@Timeout
	public void expireOrder(Timer timer) {
		Object[] info = (Object[]) timer.getInfo();
		Long orderId = (Long) info[0];
		String[] statementNames = (String[]) info[1];
		
		for (String statementName : statementNames) {
			epService.getEPAdministrator().getStatement(statementName).destroy();
		}
		
		orderService.expireOrder(orderId);
	}
}
