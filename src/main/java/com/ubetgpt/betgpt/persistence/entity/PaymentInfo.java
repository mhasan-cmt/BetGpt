package com.ubetgpt.betgpt.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_info")
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentMode;
    private String transactionId;
    private String amount;
    private String currency;
    private String email;
    private String description;
    private LocalDateTime created;
}
