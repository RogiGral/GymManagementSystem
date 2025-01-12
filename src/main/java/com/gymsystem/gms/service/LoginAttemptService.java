package com.gymsystem.gms.service;

public interface LoginAttemptService {

    void evicUserFromLoginAttemptCache(String username);
    void addUserToLoginAttemptCache(String username);
    boolean hasExceededMaxAttempts(String username);
}
