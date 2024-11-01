package com.gymsystem.gms.enumeration;


import static com.gymsystem.gms.constraints.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_COACH(COACH_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}