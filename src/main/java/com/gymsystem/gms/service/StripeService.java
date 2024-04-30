package com.gymsystem.gms.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
    PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException;
    PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException;
    PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException;
}
