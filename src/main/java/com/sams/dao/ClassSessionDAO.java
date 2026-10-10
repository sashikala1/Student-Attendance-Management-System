package com.sams.dao;

import com.sams.model.ClassSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class ClassSessionDAO {

    private static final Db.RowMapper<ClassSession> MAP = rs
            -> new ClassSession(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4),
                    rs.getDate(5).toLocalDate(), rs.getTime(6).toLocalTime(), rs.getTime(7).toLocalTime(),
                    rs.getString(8), rs.getString(9), rs.getString(10));
    private static final String SELECT = "SELECT cs.id, cs.course_id, cs.subject_id, cs.lecturer_id, "
            + "cs.session_date, cs.start_time, cs.end_time, "
            + "       c.name, s.name, l.name "
            + "FROM class_sessions cs JOIN courses c ON c.id = cs.course_id "
            + "JOIN subjects s ON s.id = cs.subject_id JOIN lecturers l ON l.id = cs.lecturer_id";
    private static final String ORDER = " ORDER BY cs.session_date DESC, cs.start_time";

    // Checks if a lecturer ID was provided:
    // 1. If it's null, return all sessions (admin view).
    // 2. If an ID exists, return only that lecturer's sessions.
    public List<ClassSession> findAll(Integer lecturerId) {
        if (lecturerId == null) {
            return Db.query(SELECT + ORDER, MAP);
        }
        return Db.query(SELECT + " WHERE cs.lecturer_id = ?" + ORDER, MAP, lecturerId);
    }

    public Optional<ClassSession> findById(int id) {
        return Db.query(SELECT + " WHERE cs.id = ?", MAP, id).stream().findFirst();
    }

    public void insert(ClassSession s) {
        Db.insert("INSERT INTO class_sessions(course_id, subject_id, lecturer_id, session_date, "
                + "start_time, end_time) "
                + "VALUES(?,?,?,?,?,?)",
                s.courseId(), s.subjectId(), s.lecturerId(), s.date(), s.start(), s.end());
    }

    public void delete(int id) {
        Db.update("DELETE FROM class_sessions WHERE id=?", id);
    }

    // Checks a specific date and time range for a lecturer.
    // Counts how many of their existing sessions happen during those exact hours to find any clashes/overlaps.
    public int countOverlap(int lecturerId, LocalDate date, LocalTime start, LocalTime end) {
        return Db.count("SELECT COUNT(*) FROM class_sessions WHERE lecturer_id=? AND session_date=? "
                + "AND start_time < ? AND end_time > ?",
                lecturerId, date, end, start);
    }

    public int countOverlapExcluding(
            int sessionId, int id, LocalDate date, LocalTime s, LocalTime e) {
        throw new UnsupportedOperationException(
                "Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void update(ClassSession classSession) {
        throw new UnsupportedOperationException(
                "Not supported yet."); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
