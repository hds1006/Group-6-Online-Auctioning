package edu.sru.cpsc.webshopping.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Value;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import edu.sru.cpsc.webshopping.domain.CustomerData;

@RestController
@RequestMapping("/api")
public class StripePayControllerAPI {
	
	@Value("${stripe.apikey}")
	String stripeKey;
	
	@RequestMapping("/createCustomer")
	public CustomerData index(@RequestBody CustomerData data) throws StripeException {
		Stripe.apiKey = stripeKey;
		Map<String, Object> params = new HashMap<>();
		params.put("name", data.getName());
		params.put("email", data.getEmail());
		Customer customer = Customer.create(params);
		data.setCustomerId(customer.getId());
		return data;
	}
}
