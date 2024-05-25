package com.gymsystem.gms.service;

import com.gymsystem.gms.exceptions.model.UserNotFoundException;

import java.math.BigDecimal;

public interface ScoreService {

    BigDecimal getUserScore(String username);
    void addPointsToScore(String username, BigDecimal score) throws UserNotFoundException;
    void removePointsFromScore(String username, BigDecimal score) throws UserNotFoundException;
}
