package com.sams.dao;

import com.sams.model.Course;
import java.util.List;

public class CourseDAO {

    private static final Db.RowMapper<Course> MAP
            = rs -> new Course(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4));

    public List<Course> findAll() {
        return Db.query("SELECT id, code, name, duration_years FROM courses ORDER BY code", MAP);
    }

    public void insert(Course c) {
        Db.insert("INSERT INTO courses(code, name, duration_years) VALUES(?,?,?)", c.code(), c.name(),
                c.years());
    }

    public void update(Course c) {
        Db.update("UPDATE courses SET code=?, name=?, duration_years=? WHERE id=?", c.code(), c.name(),
                c.years(), c.id());
    }

    public void delete(int id) {
        Db.update("DELETE FROM courses WHERE id=?", id);
    }
}
