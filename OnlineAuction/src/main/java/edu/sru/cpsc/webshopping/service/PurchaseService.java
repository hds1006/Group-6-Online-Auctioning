package edu.sru.cpsc.webshopping.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.sru.cpsc.webshopping.domain.products.PurchaseRequest;

@Service
public class PurchaseService {

    public Map<String, Object> createPaymentIntent(PurchaseRequest request) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(request.getAmount())
            .setCurrency(request.getCurrency())
            .build();

        PaymentIntent intent = PaymentIntent.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", intent.getClientSecret());
        return response;
    }
}
