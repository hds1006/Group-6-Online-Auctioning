/**
 * This file contains the StripeService class, which is responsible for processing payments using Stripe.
 * It provides a method to create a PaymentIntent object based on the provided payment amount.
 * The class uses the Stripe API to handle the payment processing.
 *
 * @author Jayden Williams
 * @version 1.0
 * @since 11-27-2024
 */
package edu.sru.cpsc.webshopping.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
/**
 * Service class for processing payments using Stripe.
 */
@Service
public class StripeService {
    private static final String STRIPE_SECRET_KEY = "sk_test_51QF5KhDuIe0xI0k5kokecMv2ASGbBXs4C8HxgD5uDTHGLR73non990TSm3t9jeRQQHMbg6y3XGhRGzrEh4ucfviG00q6h0bY4K";

    public StripeService() {
        Stripe.apiKey = STRIPE_SECRET_KEY;
    }
    /**
     * Processes a payment using Stripe.
     *
     * @param amount the payment amount as a BigDecimal
     * @return the created PaymentIntent object
     * @throws StripeException if an error occurs while processing the payment
     */
    public PaymentIntent processPayment(BigDecimal amount) throws StripeException {
        long amountInCents = amount.multiply(new BigDecimal(100)).longValue();
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency("usd")
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .setPaymentMethodOptions(
                PaymentIntentCreateParams.PaymentMethodOptions.builder()
                    .setCard(
                        PaymentIntentCreateParams.PaymentMethodOptions.Card.builder()
                            .setRequestThreeDSecure(PaymentIntentCreateParams.PaymentMethodOptions.Card.RequestThreeDSecure.AUTOMATIC)
                            .build()
                    )
                    .build()
            )
            .build();

        return PaymentIntent.create(params);
    }
    
}