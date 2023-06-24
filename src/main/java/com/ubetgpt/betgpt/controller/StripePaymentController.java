package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.paypal.dto.OrderStatus;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import com.ubetgpt.betgpt.service.UserService;
import com.ubetgpt.betgpt.stripe.Response;
import com.ubetgpt.betgpt.stripe.StripeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/stripePayment")
@Slf4j
public class StripePaymentController {
    @Value("${stripe.keys.public}")
    private String API_PUBLIC_KEY;

    private StripeService stripeService;
    @Resource
    private UserService userService;
    @Resource
    private OrderDAO orderDAO;

    public StripePaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/makePayment")
    public @ResponseBody Response createSubscription(String cardHolderName, String token, String plan, Authentication authentication) {

        if (token == null || plan.isEmpty()) {
            return new Response(false, "Stripe payment token is missing. Please try again later.");
        }

        String customerId = stripeService.createCustomer(cardHolderName, token);

        if (customerId == null) {
            return new Response(false, "An error occurred while trying to create customer");
        }

        String subscriptionId = stripeService.createSubscription(customerId, plan, authentication);

        if (subscriptionId == null) {
            return new Response(false, "An error occurred while trying to create subscription");
        }
        var out = orderDAO.findByOrderId(subscriptionId);
        out.setOrderStatus(OrderStatus.APPROVED.toString());
        com.ubetgpt.betgpt.persistence.entity.User user1 = null;
        if(authentication!= null && authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            user1 = userService.getByUserEmail(user.getAttribute("email")).orElseThrow();
        }else if(authentication!= null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User){
            org.springframework.security.core.userdetails.User user = (User) authentication.getPrincipal();
            user1 = userService.getByUserEmail(user.getUsername()).orElseThrow();
        }
        out.setUser(user1);
        orderDAO.save(out);
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
