package com.sams.dao;

import com.sams.model.Student;
import java.util.List;

public class StudentDAO {

    private static final Db.RowMapper<Student> MAP = rs
            -> new Student(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6), rs.getString(7));
    private static final String SELECT
            = "SELECT st.id, st.name, st.reg_no, st.email, st.phone, st.course_id, c.name "
            + "FROM students st JOIN courses c ON c.id = st.course_id";

    public List<Student> findAll() {
        return Db.query(SELECT + " ORDER BY st.reg_no", MAP);
    }

    public void insert(Student s) {
        Db.insert("INSERT INTO students(name, reg_no, email, phone, course_id) VALUES(?,?,?,?,?)",
                s.name(), s.regNo(), s.email(), s.phone(), s.courseId());
    }

    public void update(Student s) {
        Db.update("UPDATE students SET name=?, reg_no=?, email=?, phone=?, course_id=? WHERE id=?",
                s.name(), s.regNo(), s.email(), s.phone(), s.courseId(), s.id());
    }

    public void delete(int id) {
        Db.update("DELETE FROM students WHERE id=?", id);
    }
}
