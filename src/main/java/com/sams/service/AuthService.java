package com.sams.service;

import com.sams.dao.UserDAO;
import com.sams.model.User;
import com.sams.util.AppException;
import com.sams.util.Security;
import com.sams.util.Validator;

public class AuthService {

    private final UserDAO users = new UserDAO();

    /*Verifies credentials and starts the session. Same message for wrong
     * user/password (no hints to attackers).*/
    public User login(String username, String password) {
        String u = Validator.required(username, "Username");
        String p = Validator.required(password, "Password");
        User user = users.findByUsername(u)
                .filter(x -> x.passwordHash().equals(Security.sha256(p)))
                .orElseThrow(() -> new AppException("Invalid username or password."));
        Session.login(user);
        return user;
    }
}
