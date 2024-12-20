package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.*;
import com.gymsystem.gms.model.User;
import com.stripe.exception.StripeException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException, UserNotFoundException, StripeException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByUsernameOrEmail(String username, String email);
    User findUserById(Long userId);

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNotLocked, boolean isActive, MultipartFile profileFile) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException, StripeException;
    User updateUser(String currentUsername,String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNotLocked, boolean isActive, MultipartFile profileFile) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException;
    void deleteUser(Long id) throws IOException, StripeException;
    void resetPassword(String email) throws EmailNotFoundException;
    void setNewPassword(String email,String oldPassword,String newPassword) throws EmailNotFoundException, WrongOldPasswordException;
    User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException, UserNotFoundException;

    User findUserByCustomerId(String identifier) throws UserNotFoundException;
}