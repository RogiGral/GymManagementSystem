package com.gymsystem.gms.service.Impl;

import com.gymsystem.gms.exceptions.model.UserMembershipException;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserMembership;
import com.gymsystem.gms.repository.MembershipTypeRepository;
import com.gymsystem.gms.repository.UserMembershipRepository;
import com.gymsystem.gms.repository.UserRepository;
import com.gymsystem.gms.service.UserMembershipService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.gymsystem.gms.constraints.MembershipType.USER_ALREADY_HAS_MEMBERSHIP;
import static com.gymsystem.gms.constraints.UserImplConstant.NO_USER_FOUND;

@Service
@Transactional
public class UserMembershipImpl implements UserMembershipService {

    UserMembershipRepository userMembershipRepository;
    MembershipTypeRepository membershipTypeRepository;
    UserRepository userRepository;

    @Autowired
    public UserMembershipImpl(UserMembershipRepository userMembershipRepository, UserRepository userRepository, MembershipTypeRepository membershipTypeRepository) {
        this.userMembershipRepository = userMembershipRepository;
        this.userRepository = userRepository;
        this.membershipTypeRepository = membershipTypeRepository;
    }


    @Override
    public UserMembership getUserMembership(Long userId) throws UserNotFoundException {
        User user = checkIfUserExist(userId);
        return userMembershipRepository.getUserMembershipByUserId(user);
    }

    @Override
    public UserMembership addUserMembership(User user, MembershipType membershipType) throws UserMembershipException {
        checkIfUserHasMembership(user);
        UserMembership userMembership = new UserMembership();
        userMembership.setUserId(user);
        userMembership.setMembershipTypeId(membershipType);

        Date startDt = new Date();
        Date endDt = switch (membershipType.getValidityUnitOfTime()) {
            case DAY -> DateUtils.addDays(startDt, membershipType.getValidityPeriodNumber());
            case MONTH -> DateUtils.addMonths(startDt, membershipType.getValidityPeriodNumber());
            case YEAR -> DateUtils.addYears(startDt, membershipType.getValidityPeriodNumber());
        };
        userMembership.setStartDate(startDt);
        userMembership.setEndDate(endDt);
        userMembershipRepository.save(userMembership);
        return userMembership;
    }

    @Override
    public void deleteUserMembership(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if(user ==  null){
            throw new UserNotFoundException(NO_USER_FOUND);
        }
        userMembershipRepository.deleteAllByUserId(user);
    }


    private User checkIfUserExist(Long userId) throws UserNotFoundException {
        User user = userRepository.findUserById(userId);
        if(user ==  null){
            throw new UserNotFoundException(NO_USER_FOUND);
        }
        return user;
    }

    private void checkIfUserHasMembership(User user) throws UserMembershipException {
        UserMembership userMembership = userMembershipRepository.getUserMembershipByUserId(user);
        if(userMembership != null){
            throw new UserMembershipException(USER_ALREADY_HAS_MEMBERSHIP);
        }
    }

}
