package com.sams.service;

import com.sams.dao.LecturerDAO;
import com.sams.dao.UserDAO;
import com.sams.model.Lecturer;
import com.sams.util.AppException;
import com.sams.util.Security;
import com.sams.util.Validator;
import java.util.List;

public class LecturerService {

    private final LecturerDAO dao = new LecturerDAO();
    private final UserDAO users = new UserDAO();

    public List<Lecturer> list() {
        return dao.findAll();
    }

    public List<Integer> subjectIds(int lecturerId) {
        return dao.subjectIds(lecturerId);
    }

    /*Saves a lecturer profile and his/her subject assignments. For NEW
     * lecturers, if a username is given a LECTURER login account is created
     * too.
     */
    public void save(Lecturer l, List<Integer> subjectIds, String username, String password) {
        Session.requireAdmin();
        Lecturer clean = new Lecturer(l.id(), Validator.required(l.name(), "Name"),
                Validator.email(l.email()), Validator.phone(l.phone()), null);

        boolean createLogin = clean.id() == 0 && username != null && !username.isBlank();
        if (createLogin) {
            if (password == null || password.length() < 6) {
                throw new AppException("Login password must be at least 6 characters.");
            }
            if (users.findByUsername(username.trim()).isPresent()) {
                throw new AppException("Username '" + username.trim() + "' is already taken.");
            }
        }
        int id = clean.id();
        if (id == 0) {
            id = dao.insert(clean);
        } else {
            dao.update(clean);
        }
        dao.setSubjects(id, subjectIds);
        if (createLogin) {
            users.insert(username.trim(), Security.sha256(password), "LECTURER", id);
        }
    }

    public void delete(int id) {
        Session.requireAdmin();
        dao.delete(id);
    }
}
