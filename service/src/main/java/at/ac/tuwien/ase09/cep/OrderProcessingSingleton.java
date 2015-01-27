package at.ac.tuwien.ase09.cep;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.OrderDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.event.Deleted;
import at.ac.tuwien.ase09.model.Watch;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.service.OrderService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class OrderProcessingSingleton {

	private final ConcurrentMap<Long, OrderEntry> orderEntries = new ConcurrentHashMap<>();
	@Inject
	private OrderExpirationSingleton orderExpirationSingleton;
	@Inject
	private OrderService orderService;
	@Inject
	private OrderDataAccess orderDataAccess;
	@Inject
	private EPServiceProvider epService;
	@Resource
	private ManagedExecutorService executorService;
	
	@PostConstruct
	void init() {
		for (Order o : orderDataAccess.getActiveOrders()) {
			if (o instanceof MarketOrder) {
				addMarketOrder((MarketOrder) o);
			} else {
				addLimitOrder((LimitOrder) o);
			}
		}
	}

	public void onMarketOrderAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added MarketOrder order) {
		addMarketOrder(order);
	}
	
	public void onLimitOrderAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added LimitOrder order) {
		addLimitOrder(order);
	}
	
	public void onOrderCanceled(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Deleted Order order) {
		OrderEntry entry = orderEntries.get(order.getId());
		if (entry == null) {
			return;
		}
		
		entry.timer.cancel();
		for (EPStatement s : entry.statements) {
			s.destroy();
		}
	}
	
	public void addMarketOrder(MarketOrder order) {
		Long paperId = order.getValuePaper().getId();
		String epl = "SELECT paper.price AS price "
				+ "FROM ValuePaperPriceEntry(valuePaperId = " + paperId + ").std:lastevent() AS paper "
				+ "WHERE CURRENT_TIMESTAMP >= " + getEplDateTime(order.getValidFrom()) + " AND CURRENT_TIMESTAMP <= " + getEplDateTime(order.getValidTo());
		
		EPStatement orderStatement = epService.getEPAdministrator().createEPL(epl);
		Timer expirationTimer = orderExpirationSingleton.scheduleExpiration(order, orderStatement.getName());
		orderEntries.put(order.getId(), new OrderEntry(expirationTimer, orderStatement));
		orderStatement.addListener(new OrderListener(executorService, orderService, order, expirationTimer));
		orderStatement.start();
	}
	
	public void addLimitOrder(LimitOrder order) {
		// Process buy order when: PRICE <= limit AND NOW() >= validFrom AND NOW() <= validTo
		// Process sell order when: PRICE >= limit AND NOW() >= validFrom AND NOW() <= validTo
		// Stop limit buy order starts a limit buy order when: PRICE >= stopLimit AND NOW() >= validFrom AND NOW() <= validTo
		// Stop limit sell order starts a limit sell order when: PRICE <= stopLimit AND NOW() >= validFrom AND NOW() <= validTo
		boolean isBuy = order.getOrderAction() == OrderAction.BUY;
		Long paperId = order.getValuePaper().getId();
		
		if (order.getStopLimit() == null) {
			String epl = "SELECT paper.price AS price "
					+ "FROM ValuePaperPriceEntry(valuePaperId = " + paperId + ").std:lastevent() AS paper "
					+ "WHERE CURRENT_TIMESTAMP >= " + getEplDateTime(order.getValidFrom()) + " AND CURRENT_TIMESTAMP <= " + getEplDateTime(order.getValidTo())
					+ " AND paper.price " + (isBuy ? "<= " : ">= ") + order.getLimit();
			
			EPStatement orderStatement = epService.getEPAdministrator().createEPL(epl);
			Timer expirationTimer = orderExpirationSingleton.scheduleExpiration(order, orderStatement.getName());
			OrderEntry orderEntry = new OrderEntry(expirationTimer, orderStatement);
			orderEntries.put(order.getId(), orderEntry);
			orderStatement.addListener(new OrderListener(executorService, orderService, order, expirationTimer));
			orderStatement.start();
		} else {
			String stopEpl = "INSERT INTO StopLimitEvent "
					+ "SELECT " + order.getId() + " AS orderId, paper.price AS price "
					+ "FROM ValuePaperPriceEntry(valuePaperId = " + paperId + ").std:lastevent() AS paper "
					+ "WHERE CURRENT_TIMESTAMP >= " + getEplDateTime(order.getValidFrom()) + " AND CURRENT_TIMESTAMP <= " + getEplDateTime(order.getValidTo())
					+ " AND paper.price " + (isBuy ? ">= " : "<= ") + order.getStopLimit();
			String limitEpl = "SELECT e.price AS price "
					+ "FROM StopLimitEvent(orderId = " + order.getId() + ").std:lastevent() AS e "
					+ "WHERE CURRENT_TIMESTAMP >= " + getEplDateTime(order.getValidFrom()) + " AND CURRENT_TIMESTAMP <= " + getEplDateTime(order.getValidTo())
					+ " AND paper.price " + (isBuy ? "<= " : ">= ") + order.getLimit();

			EPStatement stopStatement = epService.getEPAdministrator().createEPL(stopEpl);
			EPStatement orderStatement = epService.getEPAdministrator().createEPL(limitEpl);
			Timer expirationTimer = orderExpirationSingleton.scheduleExpiration(order, orderStatement.getName());
			OrderEntry orderEntry = new OrderEntry(expirationTimer, orderStatement);
			orderEntries.put(order.getId(), orderEntry);
			orderStatement.addListener(new OrderListener(executorService, orderService, order, expirationTimer));
			orderStatement.start();
			stopStatement.start();
		}
	}
	
	private String getEplDateTime(Calendar cal) {
		StringBuilder sb = new StringBuilder("CURRENT_TIMESTAMP.withDate(");
		sb.append(cal.get(Calendar.YEAR));
		sb.append(',');
		sb.append(cal.get(Calendar.MONTH));
		sb.append(',');
		sb.append(cal.get(Calendar.DATE));
		sb.append(')');
		
		sb.append(".withTime(");
		sb.append(cal.get(Calendar.HOUR));
		sb.append(',');
		sb.append(cal.get(Calendar.MINUTE));
		sb.append(',');
		sb.append(cal.get(Calendar.SECOND));
		sb.append(',');
		sb.append(cal.get(Calendar.MILLISECOND));
		sb.append(')');
		
		return sb.toString();
	}
	
	private static class OrderEntry {
		private final Timer timer;
		private final EPStatement[] statements;
		
		public OrderEntry(Timer timer, EPStatement... statements) {
			this.timer = timer;
			this.statements = statements;
		}
	}
	
	private static class OrderListener implements StatementAwareUpdateListener {

		private final ManagedExecutorService executorService;
		private final OrderService orderService;
		private final Order order;
		private final Timer expirationTimer;
		
		public OrderListener(ManagedExecutorService executorService, OrderService orderService, Order order, Timer expirationTimer) {
			this.executorService = executorService;
			this.orderService = orderService;
			this.order = order;
			this.expirationTimer = expirationTimer;
		}
		
		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement, EPServiceProvider epServiceProvider) {
			EventBean latest = newEvents[newEvents.length - 1];
			BigDecimal price = (BigDecimal) latest.get("price");
			
			executorService.submit(() -> {
				expirationTimer.cancel();
				orderService.closeOrder(order.getId(), price);
			});
			
			statement.destroy();
		}
	}
}