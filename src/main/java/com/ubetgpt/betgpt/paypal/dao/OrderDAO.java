package com.ubetgpt.betgpt.paypal.dao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDAO extends JpaRepository<Order, Long> {

    Order findByPaypalOrderId(String paypalOrderId);
}
