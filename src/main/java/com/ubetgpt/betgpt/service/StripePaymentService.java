package com.ubetgpt.betgpt.service;

import com.stripe.Stripe;
import com.stripe.model.Charge;
import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import com.ubetgpt.betgpt.persistence.repository.PaymentInfoRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.ubetgpt.betgpt.enums.SubscriptionPackage.MONTHLY;

@Slf4j
@Service
public class StripePaymentService {

    @Value("${stripe.keys.private}")
    private String API_PRIVATE_KEY;

    @Resource
    PaymentInfoRepository paymentInfoRepository;

    public String createCharge(String email, String token, String subscriptionPackage) {
        String id = null;
        try {
            Stripe.apiKey = API_PRIVATE_KEY;
            Map<String, Object> chargeParams = new HashMap<>();
            if (subscriptionPackage.equals(MONTHLY)) {
                chargeParams.put("amount", 24.0);
            } else {
                chargeParams.put("amount", 200.0);
            }
            chargeParams.put("currency", "usd");
            chargeParams.put("description", "Charge for " + email);
            chargeParams.put("source", token);

            //create a charge
            Charge charge = Charge.create(chargeParams);
            id = charge.getId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoRepository.save(paymentInfo);
    }
}
