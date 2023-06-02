package com.ubetgpt.betgpt.persistence.entity;

import com.ubetgpt.betgpt.enums.Provider;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@Data
public class User {
    @Id
    private Long id;
    private String email;
    private String password;
    private Roles role;
    @Enumerated(EnumType.STRING)
    private Provider provider;
}
