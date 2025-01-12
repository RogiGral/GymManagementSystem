package com.gymsystem.gms.service.Impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.gymsystem.gms.service.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5; //CAN BE CHANGED
    private static final int ATTEMPT_INCREMENT = 1;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptServiceImpl(){
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterAccess(10,MINUTES)
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    public Integer load(String key){
                        return 0;
                    }
                });
    }

    public void evicUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }
    public void addUserToLoginAttemptCache(String username){
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage());
        }
        loginAttemptCache.put(username, attempts);
    }

    public boolean hasExceededMaxAttempts(String username){
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage());
        }

        return false;
    }


}
