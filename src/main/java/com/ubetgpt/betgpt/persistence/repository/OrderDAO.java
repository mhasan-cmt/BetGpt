package com.ubetgpt.betgpt.persistence.repository;

import com.ubetgpt.betgpt.persistence.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDAO extends JpaRepository<Order, Long> {
    Order findByOrderId(String paypalOrderId);
}
