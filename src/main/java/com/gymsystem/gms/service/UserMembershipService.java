package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.MembershipTypeNotFoundException;
import com.gymsystem.gms.exceptions.model.UserMembershipException;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.model.UserMembership;

public interface UserMembershipService {
    UserMembership getUserMembership(Long userId) throws UserNotFoundException, UserMembershipException;
    UserMembership addUserMembership(Long userId, Long membershipTypeId) throws MembershipTypeNotFoundException, UserNotFoundException, UserMembershipException;
    void deleteUserMembership(Long userId) throws UserNotFoundException;
}
