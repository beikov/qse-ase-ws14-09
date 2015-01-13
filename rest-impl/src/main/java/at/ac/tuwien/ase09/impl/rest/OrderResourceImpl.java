package at.ac.tuwien.ase09.impl.rest;

import java.net.URI;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.rest.OrderResource;
import at.ac.tuwien.ase09.rest.model.OrderDto;
import at.ac.tuwien.ase09.service.OrderService;

@Stateless
public class OrderResourceImpl implements OrderResource {
	@Inject
	private OrderService orderService;
	
	@Override
	public Response createOrder(OrderDto order) {
		Order orderEntity;
		if(order.getOrderType() == OrderType.MARKET){
			orderEntity = orderService.createMarketOrder(order.getOrderAction(), order.getValidFrom(), order.getValidTo(), order.getPortfolioId(), order.getVolume(), order.getValuePaperId());
		}else{
			orderEntity = orderService.createLimitOrder(order.getOrderAction(), order.getValidFrom(), order.getValidTo(), order.getPortfolioId(), order.getVolume(), order.getValuePaperId(), order.getLimit(), order.getStopLimit());
		}
		return Response.created(URI.create("/orders/" + orderEntity.getId())).build();
	}
	
}
