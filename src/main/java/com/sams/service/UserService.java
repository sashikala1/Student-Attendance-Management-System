package com.sams.service;

import com.sams.dao.UserDAO;
import com.sams.model.User;
import com.sams.util.AppException;
import com.sams.util.Security;
import com.sams.util.Validator;
import java.util.List;

/* User management (admin only).*/
public class UserService {

    private final UserDAO dao = new UserDAO();

    public List<User> list() {
        Session.requireAdmin();
        return dao.findAll();
    }

    public void create(String username, String password, String role, Integer lecturerId) {
        Session.requireAdmin();
        String u = Validator.required(username, "Username");
        if (password == null || password.length() < 6) {
            throw new AppException("Password must be at least 6 characters.");
        }
        if (role == null) {
            throw new AppException("Please select a role.");
        }
        if ("LECTURER".equals(role) && lecturerId == null) {
            throw new AppException("Please link this login to a lecturer.");
        }
        if (dao.findByUsername(u).isPresent()) {
            throw new AppException("Username already exists.");
        }
        dao.insert(u, Security.sha256(password), role, "LECTURER".equals(role) ? lecturerId : null);
    }

    public void delete(User target) {
        Session.requireAdmin();
        if (target.id() == Session.user().id()) {
            throw new AppException("You cannot delete your own account.");
        }
        dao.delete(target.id());
    }
}
