package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.MembershipTypeExistException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNameNotUniqueException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNotFoundException;
import com.gymsystem.gms.model.MembershipType;

import java.util.Date;
import java.util.List;

public interface MembershipTypeService {
    List<MembershipType> getMembershipTypes();
    MembershipType findMembershipTypeById(Long membershipTypeId);
    MembershipType addMembershipType(String name,String description, String type, Long price,Integer validityPeriodNumber, String validityUnitOfTime) throws MembershipTypeExistException, MembershipTypeNameNotUniqueException;
    MembershipType updateMembershipType(String oldName,String newName,String newDescription, String newType , Long newPrice,Integer validityPeriodNumber, String validityUnitOfTime) throws MembershipTypeNotFoundException, MembershipTypeNameNotUniqueException;
    MembershipType findMembershipTypeByName(String name) throws MembershipTypeNotFoundException;
    void deleteMembershipType(Long id);


}
