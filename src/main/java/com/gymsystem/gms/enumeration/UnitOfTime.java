package com.gymsystem.gms.enumeration;


import static com.gymsystem.gms.constraints.UnitOfTimeConst.*;

public enum UnitOfTime {
    DAY(UNIT_DAY),
    MONTH(UNIT_MONTH),
    YEAR(UNIT_YEAR);

    private String unitOfTime;

    UnitOfTime(String unitOfTime) {
        this.unitOfTime = unitOfTime;
    }

    public String getUnitOfTime() {
        return unitOfTime;
    }
}