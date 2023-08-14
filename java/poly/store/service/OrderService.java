package poly.store.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import poly.store.entity.Order;

public interface OrderService {
	public Order create(JsonNode orderData) ;
	
	public Order findById(Long id) ;
	
	public List<Order> findByUsername(String username) ;
}
