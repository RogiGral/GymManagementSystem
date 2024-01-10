package com.gymsystem.gms.exceptions.model;

public class WrongOldPasswordException extends Exception {
    public WrongOldPasswordException(String message) {
        super(message);
    }
}
