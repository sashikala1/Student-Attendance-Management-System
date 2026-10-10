package com.sams.util;

/*Input validation used by the Service Layer.
 * value or throws AppException.
 */
public final class Validator {

    private Validator() {
    }

    public static String required(String v, String field) {
        if (v == null || v.trim().isEmpty()) {
            throw new AppException(field + " is required.");
        }
        return v.trim();
    }

    public static String email(String v) {
        String s = required(v, "Email");
        if (!s.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new AppException("Please enter a valid email address.");
        }
        return s;
    }

    public static String phone(String v) {
        String s = required(v, "Phone");
        if (!s.matches("^[0-9+ ]{9,15}$")) {
            throw new AppException("Phone must contain 9-15 digits (e.g. 0771234567).");
        }
        return s;
    }

    public static int integer(String v, String field) {
        try {
            int n = Integer.parseInt(required(v, field));
            if (n <= 0) {
                throw new AppException(field + " must be greater than zero.");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new AppException(field + " must be a whole number.");
        }
    }
}
