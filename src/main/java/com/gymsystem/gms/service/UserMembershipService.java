package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.MembershipTypeNotFoundException;
import com.gymsystem.gms.exceptions.model.UserMembershipException;
import com.gymsystem.gms.exceptions.model.UserNotFoundException;
import com.gymsystem.gms.model.MembershipType;
import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserMembership;

public interface UserMembershipService {
    UserMembership getUserMembership(Long userId) throws UserNotFoundException, UserMembershipException;
    UserMembership addUserMembership(User user, MembershipType membershipType) throws UserMembershipException;
    void deleteUserMembership(Long userId) throws UserNotFoundException;
}
