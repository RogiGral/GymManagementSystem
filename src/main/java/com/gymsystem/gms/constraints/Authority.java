package com.gymsystem.gms.constraints;



public class Authority {
    public static final String[] USER_AUTHORITIES = {
            "user:read",
            "workout:read",
            "membershipType:read"
    };
    public static final String[] COACH_AUTHORITIES = {
            "user:read",
            "workout:read",
            "membershipType:read",
            "workout:read",
            "workout:create",
            "workout:update",
            "workout:delete"
    };
    public static final String[] ADMIN_AUTHORITIES = {
            "user:read",
            "user:create",
            "user:update",
            "user:delete",
            "workout:read",
            "workout:create",
            "workout:update",
            "workout:delete",
            "membershipType:read",
            "membershipType:create",
            "membershipType:update",
            "membershipType:delete"
    };
}
