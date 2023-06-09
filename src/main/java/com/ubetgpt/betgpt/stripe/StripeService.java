package com.ubetgpt.betgpt.stripe;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Plan;
import com.stripe.model.Subscription;
import com.ubetgpt.betgpt.persistence.entity.Order;
import com.ubetgpt.betgpt.persistence.repository.OrderDAO;
import jakarta.annotation.Resource;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@NoArgsConstructor
@Slf4j
public class StripeService {

	@Value("${stripe.keys.private}")
	private String API_SECRET_KEY;
	@Value("${stripe.plan.id.monthly}")
	private String monthlyPlanId;
	@Value("${stripe.plan.id.yearly}")
	private String yearlyPlanId;
	@Resource
	private OrderDAO orderDAO;

	public String createCustomer(String email, String token) {

		String id = null;

		try {
			Stripe.apiKey = API_SECRET_KEY;
			Map<String, Object> customerParams = new HashMap<>();
			customerParams.put("description", "Customer for " + email);
			customerParams.put("email", email);
			// obtained with stripe.js
			customerParams.put("source", token);

			Customer customer = Customer.create(customerParams);
			id = customer.getId();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return id;
	}

	public String createSubscription(String customerId, String plan) {

		String subscriptionId = null;

		try {
			Stripe.apiKey = API_SECRET_KEY;

			Map<String, Object> item = new HashMap<>();
			String planId = Objects.equals(plan, "Monthly") ? monthlyPlanId : yearlyPlanId;
			Plan  planSearch =Plan.retrieve(planId);
			if (planSearch!=null){
			item.put("plan", Objects.equals(plan, "Monthly") ? monthlyPlanId : yearlyPlanId);
			}else{
				throw new IllegalStateException("Plan not found");
			}

			Map<String, Object> items = new HashMap<>();
			items.put("0", item);

			Map<String, Object> params = new HashMap<>();
			params.put("customer", customerId);
			params.put("items", items);

			Subscription subscription = Subscription.create(params);
			Order order = new Order();
			order.setOrderId(subscription.getId());
			order.setOrderStatus(subscription.getStatus());
			order.setCreatedAt(LocalDate.now());
			order.setExpired_at(Objects.equals(plan, "Monthly") ?  LocalDate.now().plusMonths(1) : LocalDate.now().plusYears(1));
			var out = orderDAO.save(order);
			log.info("Saved order: {}", out);
			subscriptionId = subscription.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subscriptionId;
	}
	
	public boolean cancelSubscription(String subscriptionId) {
		
		boolean subscriptionStatus;
		
		try {
			Subscription subscription = Subscription.retrieve(subscriptionId);
			subscription.cancel();
			subscriptionStatus = true;	
		} catch (Exception e) {
			e.printStackTrace();
			subscriptionStatus = false;
		}
		return subscriptionStatus;
	}

}
