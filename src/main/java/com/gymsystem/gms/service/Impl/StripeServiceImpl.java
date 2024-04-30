package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class StripeServiceImpl implements StripeService {


    @Value("${api.stripe.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }


    @Override
    public PaymentIntent createPaymentIntent(Long amount, String currency, String customerId) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount * 100)
                .setCurrency(currency)
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                .setPaymentMethod("pm_card_visa")
                .setCustomer(customerId)
                .build();
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent;
    }

    @Override
    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent resource = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setReturnUrl("https://www.example.com")
                        .build();
        PaymentIntent paymentIntent = resource.confirm(params);
        return paymentIntent;
    }

    @Override
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent resource = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
        PaymentIntent paymentIntent = resource.cancel(params);
        return paymentIntent;
    }
}
