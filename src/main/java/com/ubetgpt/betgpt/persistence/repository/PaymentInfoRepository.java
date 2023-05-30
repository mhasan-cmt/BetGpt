package com.ubetgpt.betgpt.persistence.repository;

import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
    PaymentInfo findByEmail(String email);


}
