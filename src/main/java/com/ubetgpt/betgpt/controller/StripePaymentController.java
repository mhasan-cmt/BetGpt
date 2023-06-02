package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.enums.PaymentModeEnum;
import com.ubetgpt.betgpt.model.Response;
import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import com.ubetgpt.betgpt.service.StripePaymentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

import static com.ubetgpt.betgpt.enums.SubscriptionPackage.MONTHLY;

@Controller
@RequestMapping("/stripePayment")
@AllArgsConstructor
public class StripePaymentController {

    private final StripePaymentService stripePaymentService;

    @PostMapping
    public @ResponseBody
    Response createCharge(String email, String subscriptionPackage, String token) {
        if (token == null) {
            return new Response(false, "Stripe payment token is missing. Please, try again later.");
        }
        String chargeId = stripePaymentService.createCharge(email, token, subscriptionPackage);
        if (chargeId == null) {
            return new Response(false, "An error occurred while trying to create a charge.");
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentMode(PaymentModeEnum.STRIPE.name());
        paymentInfo.setTransactionId(chargeId);
        if (subscriptionPackage.equals(MONTHLY)) {
            paymentInfo.setAmount("24.0");
        } else {
            paymentInfo.setAmount("200.0");
        }
        paymentInfo.setCurrency("USD");
        paymentInfo.setEmail(email);
        paymentInfo.setCreated(LocalDateTime.now());
        stripePaymentService.savePaymentInfo(paymentInfo);
        return new Response(true, String.format("Success! Your transaction id : %s", chargeId));
    }
}
