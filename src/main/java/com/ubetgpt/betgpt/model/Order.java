package com.ubetgpt.betgpt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {
    private double amount;
    private String currency;
    private String method;
    private String intent;
    private String description;
}
