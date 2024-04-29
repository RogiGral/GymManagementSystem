package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserMembership;
import com.gymsystem.gms.service.MembershipTypeService;
import com.gymsystem.gms.service.StripeService;
import com.gymsystem.gms.service.UserMembershipService;
import com.gymsystem.gms.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping(value ="/userMembership")
@AllArgsConstructor
public class UserMembershipController extends ExceptionHandling {

    @Autowired
    UserMembershipService userMembershipService;

    @Autowired
    StripeService stripeService;

    @Autowired
    UserService userService;

    @Autowired
    MembershipTypeService membershipTypeService;

    @PostMapping("/add")
    public ResponseEntity<UserMembership> assignUserToMembership(@RequestParam("userId") Long userId,
                                                                 @RequestParam("membershipTypeId") Long membershipTypeId) throws UserMembershipException, StripeException {
        MembershipType membershipType = membershipTypeService.findMembershipTypeById(membershipTypeId);
        User user = userService.findUserById(userId);
        UserMembership userMembership = userMembershipService.addUserMembership(user,membershipType);
        return new ResponseEntity<>(userMembership, OK);
    }
    @PostMapping("/create_payment_intent")
    public ResponseEntity<String> createPaymentIntent(@RequestParam("membershipPrice") Long membershipPrice,
                                                   @RequestParam("currency") String currency) throws StripeException {
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(membershipPrice,currency);
        System.out.println(paymentIntent);
        return new ResponseEntity<>(paymentIntent.toJson(), OK);
    }
    @PostMapping("/confirm_payment")
    public ResponseEntity<String> confirmPaymentIntent(@RequestParam("paymentIntentId") String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = stripeService.confirmPaymentIntent(paymentIntentId);
        return new ResponseEntity<>(paymentIntent.toJson(), OK);
    }
    @PostMapping("/cancel_payment")
    public ResponseEntity<String> cancelPaymentIntent(@RequestParam("paymentIntentId") String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = stripeService.cancelPaymentIntent(paymentIntentId);
        return new ResponseEntity<>(paymentIntent.toJson(), OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserMembership> getUserMembership(@PathVariable("userId") Long userId) throws UserNotFoundException, UserMembershipException {
        UserMembership userMembership = userMembershipService.getUserMembership(userId);
        return new ResponseEntity<>(userMembership, OK);
    }
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<HttpResponse> deleteUserMembership(@PathVariable("userId") Long userId) throws UserNotFoundException {
        userMembershipService.deleteUserMembership(userId);
        return response(OK, "USER_MEMBERSHIP_DELETED");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
