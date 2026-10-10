package com.sams.dao;

/* Counts used by the dashboard cards. */
public class StatsDAO {

    public int countCourses() {
        return Db.count("SELECT COUNT(*) FROM courses");
    }

    public int countStudents() {
        return Db.count("SELECT COUNT(*) FROM students");
    }

    public int countLecturers() {
        return Db.count("SELECT COUNT(*) FROM lecturers");
    }

    public int countToday() {
        return Db.count("SELECT COUNT(*) FROM class_sessions WHERE session_date = CURDATE()");
    }
}
