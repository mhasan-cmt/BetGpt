package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.enums.PaymentModeEnum;
import com.ubetgpt.betgpt.model.Response;
import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import com.ubetgpt.betgpt.service.StripePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/stripePayment")
public class StripePaymentController {


    @Autowired
    private StripePaymentService stripePaymentService;

    @Value("${stripe.keys.public}")
    private String stripePublicKey;

    @GetMapping("/view")
    public ModelAndView view() {
        ModelAndView model = new ModelAndView();
        model.setViewName("stripe-payment");
        model.addObject("stripePublicKey",stripePublicKey);
        return model;
    }

    @PostMapping("/makePayment")
    public @ResponseBody
    Response createCharge(String email, double amount, String token) {
        if (token == null) {
            return new Response(false, "Stripe payment token is missing. Please, try again later.");
        }
        String chargeId = stripePaymentService.createCharge(email, token, (long) amount*100);
        if (chargeId == null) {
            return new Response(false, "An error occurred while trying to create a charge.");
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentMode(PaymentModeEnum.STRIPE.name());
        paymentInfo.setTransactionId(chargeId);
        paymentInfo.setAmount(String.valueOf(amount));
        paymentInfo.setCurrency("USD");
        paymentInfo.setEmail(email);
        paymentInfo.setCreated(LocalDateTime.now());
        stripePaymentService.savePaymentInfo(paymentInfo);
        return new Response(true, "Success! Your transaction id : " + chargeId);
    }
}
