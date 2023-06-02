package com.ubetgpt.betgpt.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.ubetgpt.betgpt.enums.PaymentModeEnum;
import com.ubetgpt.betgpt.model.Order;
import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import com.ubetgpt.betgpt.service.PaypalPaymentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

import static com.ubetgpt.betgpt.enums.SubscriptionPackage.MONTHLY;

@Controller
@RequestMapping("/paypalPayment")
public class PaypalPaymentController {

    @Resource
    PaypalPaymentService paypalPaymentService;

    public static final String SUCCESS_URL = "success";
    public static final String CANCEL_URL = "cancel";

    @GetMapping("/view")
    public String view() {
        return "paypal-payment";
    }

    @PostMapping("/makePayment")
    public RedirectView payment(@RequestParam("subscription_package") String subscriptionPackage,
                                @RequestParam("description") String description) {
        Order order = new Order();
        if (subscriptionPackage.equals(MONTHLY)){
            order.setAmount(24.0);
        }else {
            order.setAmount(200.0);
        }
        order.setCurrency("USD");
        order.setMethod("paypal");
        order.setIntent("sale");
        order.setDescription(description);
        RedirectView redirectView = new RedirectView();
        try {
            Payment payment = paypalPaymentService.createPayment(order.getAmount(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(), "http://localhost:8080/paypalPayment/" + CANCEL_URL,
                    "http://localhost:8080/paypalPayment/" + SUCCESS_URL);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    redirectView.setUrl(link.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return redirectView;
    }

    @GetMapping(value = SUCCESS_URL)
    public String successPay(Model model,@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalPaymentService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setPaymentMode(PaymentModeEnum.PAYPAL.name());
                paymentInfo.setTransactionId(payment.getId());
                paymentInfo.setAmount(payment.getTransactions().get(0).getAmount().getTotal());
                paymentInfo.setCurrency("USD");
                paymentInfo.setEmail(payment.getPayer().getPayerInfo().getEmail());
                paymentInfo.setDescription(payment.getTransactions().get(0).getDescription());
                paymentInfo.setCreated(LocalDateTime.now());
                paypalPaymentService.savePaymentInfo(paymentInfo);

                model.addAttribute("message", "Payment successfully");
                model.addAttribute("transactionId", payment.getId());
            } else {
                model.addAttribute("message", "Payment failed");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return "index";
    }

    @GetMapping(value = CANCEL_URL)
    public String getCancelUrl(Model model) {
        model.addAttribute("message", "Payment cancelled");
        return "index";
    }

}
