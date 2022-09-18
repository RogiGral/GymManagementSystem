package com.gymsystem.gms.controller;


import com.gymsystem.gms.exceptions.ExceptionHandling;
import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.HttpResponse;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.model.Workout;
import com.gymsystem.gms.service.MembershipTypeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<MembershipType>> getAllMembershipTypes() {
        List<MembershipType> membershipTypes = membershipTypeService.getMembershipTypes();
        return new ResponseEntity<>(membershipTypes, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<MembershipType> addNewMembershipType( @RequestParam("name") String name,
                                                                @RequestParam("price") Long price,
                                                                @RequestParam("numberOfMonths")Integer numberOfMonths) throws MembershipTypeExistException, MembershipTypeNameNotUniqueException {
        MembershipType membershipType = membershipTypeService.addMembershipType(name,price,numberOfMonths);
        return new ResponseEntity<>(membershipType, OK);
    }
    @PostMapping("/update")
    public ResponseEntity<MembershipType> updateMembershipType( @RequestParam("oldName") String oldName,
                                                                @RequestParam("newName") String newName,
                                                                @RequestParam("newPrice") Long newPrice,
                                                                @RequestParam("numberOfMonths")Integer numberOfMonths) throws MembershipTypeNotFoundException, MembershipTypeNameNotUniqueException {
        MembershipType membershipType = membershipTypeService.updateMembershipType(oldName,newName,newPrice,numberOfMonths);
        return new ResponseEntity<>(membershipType, OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") Long id) {
        membershipTypeService.deleteMembershipType(id);
        return response(OK, "MEMBERSHIP_TYPE_DELETED_SUCCESSFULLY");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message), httpStatus);
    }


}
