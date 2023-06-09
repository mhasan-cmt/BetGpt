package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.paypal.PayPalHttpClient;
import com.ubetgpt.betgpt.persistence.entity.Order;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import com.ubetgpt.betgpt.paypal.dto.*;
import com.ubetgpt.betgpt.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Controller
@RequestMapping(value = "/checkout")
@Slf4j
public class CheckoutController {
    @Resource
    private UserService userService;

    private final PayPalHttpClient payPalHttpClient;
    private final OrderDAO orderDAO;

    @Value("${stripe.keys.public}")
    private String stripePublicKey;

    @Autowired
    public CheckoutController(PayPalHttpClient payPalHttpClient, OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.payPalHttpClient = payPalHttpClient;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderResponseDTO> checkout(@RequestBody OrderRequest orderRequest, Authentication authentication) throws Exception {
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
        com.ubetgpt.betgpt.persistence.entity.User user1 = null;
        if(authentication!= null && authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();
            user1 = userService.getByUserEmail(user.getAttribute("email")).orElseThrow();
        }else if(authentication!= null && authentication.getPrincipal() instanceof User){
            User user = (User) authentication.getPrincipal();
            user1 = userService.getByUserEmail(user.getUsername()).orElseThrow();
        }
        entity.setUser(user1);
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
    public String paymentSuccess(HttpServletRequest request, Model model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        model.addAttribute("stripePublicKey",stripePublicKey);
        if(securityContext.getAuthentication().getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User user = (DefaultOAuth2User) securityContext.getAuthentication().getPrincipal();
            model.addAttribute("userDetails", user.getAttribute("name")!= null ?user.getAttribute("name"):user.getAttribute("login"));
            Order order = orderDAO.findByUserEmail(user.getAttribute("email"));
            model.addAttribute("paid", order!=null);
        } else {
            User user = (User) securityContext.getAuthentication().getPrincipal();
//            com.oauth.implementation.model.User users = userRepo.findByEmail(user.getUsername());
//            model.addAttribute("userDetails", users.getName());
        }
        var orderId = request.getParameter("token");
        var out = orderDAO.findByOrderId(orderId);
        out.setOrderStatus(OrderStatus.APPROVED.toString());
        orderDAO.save(out);
        return "index";
    }
}
