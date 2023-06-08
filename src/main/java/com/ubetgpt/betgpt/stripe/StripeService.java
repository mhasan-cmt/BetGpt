package com.ubetgpt.betgpt.stripe;

import com.stripe.Stripe;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

	@Value("${stripe.keys.private}")
	private String API_SECRET_KEY;

	public StripeService() {

	}

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
			item.put("plan", plan);

			Map<String, Object> items = new HashMap<>();
			items.put("0", item);

			Map<String, Object> params = new HashMap<>();
			params.put("customer", customerId);
			params.put("items", items);

			Subscription subscription = Subscription.create(params);

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
