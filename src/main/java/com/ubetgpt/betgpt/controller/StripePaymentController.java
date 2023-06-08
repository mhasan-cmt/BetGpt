package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.stripe.Response;
import com.ubetgpt.betgpt.stripe.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/stripePayment")
public class StripePaymentController {
    @Value("${stripe.keys.public}")
    private String API_PUBLIC_KEY;

    private StripeService stripeService;

    public StripePaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/makePayment")
    public @ResponseBody Response createSubscription(String cardHolderName, String token, String plan) {

        if (token == null || plan.isEmpty()) {
            return new Response(false, "Stripe payment token is missing. Please try again later.");
        }

        String customerId = stripeService.createCustomer(cardHolderName, token);

        if (customerId == null) {
            return new Response(false, "An error occurred while trying to create customer");
        }

        String subscriptionId = stripeService.createSubscription(customerId, plan);

        if (subscriptionId == null) {
            return new Response(false, "An error occurred while trying to create subscription");
        }

        return new Response(true, "Success! your subscription id is " + subscriptionId);
    }

    @PostMapping("/cancel-subscription")
    public @ResponseBody Response cancelSubscription(String subscriptionId) {

        boolean subscriptionStatus = stripeService.cancelSubscription(subscriptionId);

        if (!subscriptionStatus) {
            return new Response(false, "Failed to cancel subscription. Please try again later");
        }

        return new Response(true, "Subscription cancelled successfully");
    }
}
