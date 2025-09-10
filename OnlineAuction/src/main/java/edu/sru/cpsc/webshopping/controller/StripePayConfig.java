package edu.sru.cpsc.webshopping.controller;

import org.springframework.context.annotation.Configuration;
import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripePayConfig {
	
	@PostConstruct
    public void init() {
        Stripe.apiKey = "sk_test_51QF5KhDuIe0xI0k5kokecMv2ASGbBXs4C8HxgD5uDTHGLR73non990TSm3t9jeRQQHMbg6y3XGhRGzrEh4ucfviG00q6h0bY4K";
    }
}
