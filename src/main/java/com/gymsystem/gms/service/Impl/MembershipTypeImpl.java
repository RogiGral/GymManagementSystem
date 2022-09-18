package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.exceptions.model.MembershipTypeExistException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNameNotUniqueException;
import com.gymsystem.gms.exceptions.model.MembershipTypeNotFoundException;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.repository.MembershipTypeRepository;
import com.gymsystem.gms.service.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.gymsystem.gms.constraints.MembershipType.*;

@Service
@Transactional
public class MembershipTypeImpl implements MembershipTypeService {

    MembershipTypeRepository membershipTypeRepository;

    @Autowired
    public MembershipTypeImpl(MembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public List<MembershipType> getMembershipTypes() {
        return membershipTypeRepository.findAll();
    }

    @Override
    public MembershipType addMembershipType(String name, Long price, Integer numberOfMonths) throws MembershipTypeExistException, MembershipTypeNameNotUniqueException {
        checkIfNameIsUnique(name);
        MembershipType membershipType = checkIfMembershipTypeExists(name,price);
        membershipType.setName(name);
        membershipType.setPrice(price);
        membershipType.setNumberOfMonths(numberOfMonths);
        membershipTypeRepository.save(membershipType);
        return membershipType;
    }

    @Override
    public MembershipType updateMembershipType(String oldName, String newName, Long newPrice, Integer newNumberOfMonths ) throws MembershipTypeNotFoundException, MembershipTypeNameNotUniqueException {
        checkIfNameIsUnique(newName);
        MembershipType membershipType = findMembershipTypeByName(oldName);
        membershipType.setName(newName);
        membershipType.setPrice(newPrice);
        membershipType.setNumberOfMonths(newNumberOfMonths);
        membershipTypeRepository.save(membershipType);
        return membershipType;
    }

    @Override
    public MembershipType findMembershipTypeByName(String name) throws MembershipTypeNotFoundException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByName(name);
        if(membershipType ==  null){
            throw new MembershipTypeNotFoundException(MEMBERSHIP_TYPE_NOT_FOUND);
        }
        return membershipType;
    }

    @Override
    public void deleteMembershipType(Long id) {
        membershipTypeRepository.deleteById(id);
    }


    private MembershipType checkIfMembershipTypeExists(String name, Long price) throws MembershipTypeExistException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByNameAndPrice(name,price);
        if(membershipType!=null){
            throw new MembershipTypeExistException(MEMBERSHIP_TYPE_ALREADY_EXISTS);
        }
        else{
            return new MembershipType();
        }
    }
    private void checkIfNameIsUnique(String name) throws MembershipTypeNameNotUniqueException {
        MembershipType membershipType = membershipTypeRepository.findMembershipTypeByName(name);
        if(membershipType !=  null){
            throw new MembershipTypeNameNotUniqueException(MEMBERSHIP_NAME_IS_NOT_UNIQUE);
        }
    }
}
