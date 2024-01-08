package com.gymsystem.gms.service;

public interface LoginAttemptService {

    void evicUserFromLoginAttemptCache(String username);
    void addUserToLoginAttemptAcche(String username);
    boolean hasExceededMaxAttempts(String username);
}
