package com.sams.dao;

import com.sams.model.Subject;
import java.util.List;

public class SubjectDAO {

    private static final Db.RowMapper<Subject> MAP = rs
            -> new Subject(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getString(5));
    private static final String SELECT = "SELECT s.id, s.code, s.name, s.course_id, c.name FROM "
            + "subjects s JOIN courses c ON c.id = s.course_id";

    public List<Subject> findAll() {
        return Db.query(SELECT + " ORDER BY c.code, s.code", MAP);
    }

    public List<Subject> findByCourse(int courseId) {
        return Db.query(SELECT + " WHERE s.course_id = ? ORDER BY s.code", MAP, courseId);
    }

    /*Subjects a lecturer is assigned to teach.*/
    public List<Subject> findByLecturer(int lecturerId) {
        return Db.query(SELECT
                + (" JOIN lecturer_subjects ls ON ls.subject_id = s.id WHERE ls.lecturer_id = ? ORDER "
                + "BY s.code"),
                MAP, lecturerId);
    }

    public void insert(Subject s) {
        Db.insert("INSERT INTO subjects(code, name, course_id) VALUES(?,?,?)", s.code(), s.name(),
                s.courseId());
    }

    public void update(Subject s) {
        Db.update("UPDATE subjects SET code=?, name=? WHERE id=?", s.code(), s.name(), s.id());
    }

    public void delete(int id) {
        Db.update("DELETE FROM subjects WHERE id=?", id);
    }
}
