package poly.store.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import poly.store.dao.OrderDAO;
import poly.store.dao.OrderDetailDAO;
import poly.store.entity.Order;
import poly.store.entity.OrderDetail;
import poly.store.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService{
	@Autowired
	OrderDAO dao;
	
	@Autowired
	OrderDetailDAO ddao;
	
	public Order create(JsonNode orderData) {
		//OM đổi json thành những đối tượng cần thiết
		ObjectMapper mapper = new ObjectMapper();
		
		Order order = mapper.convertValue(orderData, Order.class);
		dao.save(order);
		
		TypeReference<List<OrderDetail>> type = new TypeReference<List<OrderDetail>>() {};
		//1.convertValue đọc json sang list order detail
		List<OrderDetail> details = mapper.convertValue(orderData.get("orderDetails"), type)
				//2.setOrder đê lưu 1 lúc nhiều order detail
				.stream().peek(d -> d.setOrder(order)).collect(Collectors.toList());
		ddao.saveAll(details);
		
		return order;
	}
	
	public Order findById(Long id) {
		return dao.findById(id).get();
	}
	
	public List<Order> findByUsername(String username) {
		return dao.findByUsername(username);
	}
}
