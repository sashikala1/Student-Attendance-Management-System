package com.sams.service;

import com.sams.dao.CourseDAO;
import com.sams.dao.SubjectDAO;
import com.sams.model.Course;
import com.sams.model.Subject;
import com.sams.util.Validator;
import java.util.List;

/*Business logic for courses and the subjects that belong to them.*/
public class CourseService {

    private final CourseDAO courses = new CourseDAO();
    private final SubjectDAO subjects = new SubjectDAO();

    public List<Course> courses() {
        return courses.findAll();
    }

    public List<Subject> allSubjects() {
        return subjects.findAll();
    }

    public List<Subject> subjects(int courseId) {
        return subjects.findByCourse(courseId);
    }

    /**
     * id == 0 means new record, otherwise update.
     */
    public void saveCourse(Course c) {
        Session.requireAdmin();
        Course clean = new Course(c.id(), Validator.required(c.code(), "Course code").toUpperCase(),
                Validator.required(c.name(), "Course name"), c.years());
        if (clean.id() == 0) {
            courses.insert(clean);
        } else {
            courses.update(clean);
        }
    }

    public void deleteCourse(int id) {
        Session.requireAdmin();
        courses.delete(id);
    }

    public void saveSubject(Subject s) {
        Session.requireAdmin();
        Subject clean = new Subject(s.id(), Validator.required(s.code(), "Subject code").toUpperCase(),
                Validator.required(s.name(), "Subject name"), s.courseId(), null);
        if (clean.id() == 0) {
            subjects.insert(clean);
        } else {
            subjects.update(clean);
        }
    }

    public void deleteSubject(int id) {
        Session.requireAdmin();
        subjects.delete(id);
    }
}
