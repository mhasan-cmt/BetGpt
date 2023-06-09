package com.ubetgpt.betgpt.paypal;

import com.ubetgpt.betgpt.persistence.entity.Order;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import com.ubetgpt.betgpt.paypal.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping(value = "/checkout")
@Slf4j
public class CheckoutController {

    private final PayPalHttpClient payPalHttpClient;
    private final OrderDAO orderDAO;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderRequest orderRequest) throws Exception {
        var orderDTO = new OrderDTO();
        orderDTO.setIntent(OrderIntent.CAPTURE);
        orderDTO.setPurchaseUnits(List.of(new PurchaseUnit(MoneyDTO.builder()
                .currencyCode("USD")
                .value(Objects.equals(orderRequest.subscriptionPackage(), "basic") ? "24.00" : "200.00")
                .build())));
        var appContext = new PayPalAppContextDTO();
        appContext.setReturnUrl("http://localhost:8080/checkout/success");
        appContext.setBrandName("BetGPT");
        appContext.setLandingPage(PaymentLandingPage.BILLING);
        orderDTO.setApplicationContext(appContext);
        var orderResponse = payPalHttpClient.createOrder(orderDTO);

        var entity = new Order();
        entity.setOrderId(orderResponse.getId());
        entity.setOrderStatus(orderResponse.getStatus().toString());
        entity.setCreatedAt(LocalDate.now());
        entity.setSubscriptionPackage(orderRequest.subscriptionPackage());
        entity.setExpired_at(Objects.equals(orderRequest.subscriptionPackage(), "basic") ?  LocalDate.now().plusMonths(1) : LocalDate.now().plusYears(1));
        orderResponse.getLinks().forEach(linkDTO -> {
            if (linkDTO.getRel().equals("approve")) {
                entity.setApproveUrl(linkDTO.getHref());
            }
        });

        var out = orderDAO.save(entity);
        log.info("Saved order: {}", out);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping(value = "/success")
    public String paymentSuccess(HttpServletRequest request) {
        var orderId = request.getParameter("token");
        var out = orderDAO.findByOrderId(orderId);
        out.setOrderStatus(OrderStatus.APPROVED.toString());
        orderDAO.save(out);
        return "index";
    }
}
