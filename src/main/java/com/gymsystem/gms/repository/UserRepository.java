package com.gymsystem.gms.repository;

import com.gymsystem.gms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //TODO change it to optional rules
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUsernameOrEmail(String username, String email);
    User findUserById(Long id);
    Optional<User> findUserByUserId(String userId);
}
