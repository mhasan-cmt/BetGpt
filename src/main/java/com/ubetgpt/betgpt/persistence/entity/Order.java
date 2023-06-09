package com.ubetgpt.betgpt.persistence.entity;

import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "order_status")
    private String orderStatus;
    @Column(name = "approve_url")
    private String approveUrl;
    @Column(name = "subscription_package")
    private String subscriptionPackage;
    @Column(name = "created_at")
    private LocalDate createdAt;
    @Column(name = "expiredAt")
    private LocalDate expired_at;
    @OneToOne
    private User user;
}
