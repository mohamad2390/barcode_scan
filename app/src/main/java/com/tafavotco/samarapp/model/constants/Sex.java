package com.tafavotco.samarapp.model.constants;

public enum Sex {

    UNKNOWN(0),
    MALE(1),
    FEMALE(2);

    public final int VALUE;

    Sex(int value) {
        VALUE = value;
    }

    public static Sex findByValue(Integer value) {
        if(value == null){
            return UNKNOWN;
        }
        for (Sex status : Sex.values()) {
            if (status.VALUE == value) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
