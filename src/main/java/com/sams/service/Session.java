package com.sams.service;

import com.sams.model.User;

/*Holds the currently logged-in user for the whole application.*/
public final class Session {

    private static User current;

    private Session() {
    }

    public static void login(User u) {
        current = u;
    }

    public static void logout() {
        current = null;
    }

    public static User user() {
        if (current == null) {
            throw new com.sams.util.AppException("You are not logged in.");
        }
        return current;
    }

    public static boolean isLoggedIn() {
        return current != null;
    }

    /*Role-based access control: throws unless the current user is an ADMIN.*/
    public static void requireAdmin() {
        if (!user().isAdmin()) {
            throw new com.sams.util.AppException("Access denied: administrator privileges required.");
        }
    }

    /*Lecturer id of the logged-in lecturer account.*/
    public static int lecturerId() {
        Integer id = user().lecturerId();
        if (id == null) {
            throw new com.sams.util.AppException("This account is not linked to a lecturer profile.");
        }
        return id;
    }
}
