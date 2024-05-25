package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.User;
import com.gymsystem.gms.model.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership,Long> {
    UserMembership getUserMembershipByUserId(User userId);
    void deleteAllByUserId(User userId);
}
