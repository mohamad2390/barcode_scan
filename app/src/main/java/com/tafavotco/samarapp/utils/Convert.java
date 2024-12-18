package com.tafavotco.samarapp.utils;

public class Convert {
    public static boolean toBoolean(Object obj) {
        return toBoolean(obj, false);
    }

    public static boolean toBoolean(Object obj, boolean defaultValue) {
        if (obj == null) {
            return defaultValue;
        }

        if (obj instanceof String) {
            String val = obj.toString().trim();
            return !(val.equalsIgnoreCase("false") || val.equals("0") || val.equalsIgnoreCase("null") || val.isEmpty());
        }

        if (obj instanceof Number) {
            return !obj.equals(0);
        }

        try {
            return Boolean.parseBoolean(String.valueOf(obj));
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
