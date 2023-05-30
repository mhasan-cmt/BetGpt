package com.ubetgpt.betgpt.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.ubetgpt.betgpt.enums.PaymentModeEnum;
import com.ubetgpt.betgpt.model.Order;
import com.ubetgpt.betgpt.persistence.entity.PaymentInfo;
import com.ubetgpt.betgpt.service.PaypalPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/paypalPayment")
public class PaypalPaymentController {

    @Autowired
    PaypalPaymentService paypalPaymentService;

    public static final String SUCCESS_URL = "success";
    public static final String CANCEL_URL = "cancel";

    @GetMapping("/view")
    public String view() {
        return "paypal-payment";
    }

    @PostMapping("/makePayment")
    public RedirectView payment(@RequestParam("amount") String amount,
                                @RequestParam("currency") String currency,
                                @RequestParam("description") String description) {
        Order order = new Order();
        order.setAmount(Double.valueOf(amount));
        order.setCurrency(currency);
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
    public ModelAndView successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        ModelAndView model = new ModelAndView();
        try {
            Payment payment = paypalPaymentService.executePayment(paymentId, payerId);
//            System.out.println(payment.toJSON());
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

                model.setViewName("success");
                model.addObject("transactionId", payment.getId());
            } else {
                model.setViewName("failed");
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return model;
    }

    @GetMapping(value = CANCEL_URL)
    public String getCancelUrl() {
        return "cancel";
    }


}
