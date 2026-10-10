package com.sams.service;

import com.sams.dao.StudentDAO;
import com.sams.model.Student;
import com.sams.util.AppException;
import com.sams.util.Validator;
import java.util.List;

public class StudentService {

    private final StudentDAO dao = new StudentDAO();

    public List<Student> list() {
        return dao.findAll();
    }

    public void save(Student s) {
        Session.requireAdmin();
        if (s.courseId() <= 0) {
            throw new AppException("Please select a course.");
        }
        Student clean = new Student(s.id(), Validator.required(s.name(), "Name"),
                Validator.required(s.regNo(), "Registration number").toUpperCase(),
                Validator.email(s.email()), Validator.phone(s.phone()), s.courseId(), null);
        if (clean.id() == 0) {
            dao.insert(clean);
        } else {
            dao.update(clean);
        }
    }

    public void delete(int id) {
        Session.requireAdmin();
        dao.delete(id);
    }
}
