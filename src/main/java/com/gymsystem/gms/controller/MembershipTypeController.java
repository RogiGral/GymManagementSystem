package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.service.MembershipTypeService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value ="/membershipType")
@AllArgsConstructor
public class MembershipTypeController extends ExceptionHandling {

    @Autowired
    MembershipTypeService membershipTypeService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('membershipType:read')") //comment for testing
    public ResponseEntity<List<MembershipType>> getAllMembershipTypes() {
        List<MembershipType> membershipTypes = membershipTypeService.getMembershipTypes();
        return new ResponseEntity<>(membershipTypes, OK);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('membershipType:create')") //comment for testing
    public ResponseEntity<MembershipType> addNewMembershipType( @RequestParam("name") String name,
                                                                @RequestParam("description") String description,
                                                                @RequestParam("type") String type,
                                                                @RequestParam("price") Long price,
                                                                @RequestParam("validityPeriodNumber")Integer validityPeriodNumber,
                                                                @RequestParam("validityUnitOfTime")String validityUnitOfTime) throws MembershipTypeExistException, MembershipTypeNameNotUniqueException, StripeException {
        MembershipType membershipType = membershipTypeService.addMembershipType(name,description,type,price,validityPeriodNumber,validityUnitOfTime);
        return new ResponseEntity<>(membershipType, OK);
    }
    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('membershipType:update')") //comment for testing
    public ResponseEntity<MembershipType> updateMembershipType( @RequestParam("oldName") String oldName,
                                                                @RequestParam("name") String newName,
                                                                @RequestParam("description") String newDescription,
                                                                @RequestParam("type") String newType,
                                                                @RequestParam("price") Long newPrice,
                                                                @RequestParam("validityPeriodNumber")Integer validityPeriodNumber,
                                                                @RequestParam("validityUnitOfTime")String validityUnitOfTime) throws MembershipTypeNotFoundException, MembershipTypeNameNotUniqueException {
        MembershipType membershipType = membershipTypeService.updateMembershipType(oldName,newName,newDescription,newType,newPrice,validityPeriodNumber,validityUnitOfTime);
        return new ResponseEntity<>(membershipType, OK);
    }
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('membershipType:delete')") //comment for testing
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") Long id) throws StripeException {
        membershipTypeService.deleteMembershipType(id);
        return response(OK, "MEMBERSHIP_TYPE_DELETED_SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
