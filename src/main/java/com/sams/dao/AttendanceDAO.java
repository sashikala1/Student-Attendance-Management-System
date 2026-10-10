package com.sams.dao;

import com.sams.model.AttendanceRow;
import com.sams.model.ReportRow;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

// Gets a list of every student in this course session.
// Includes their attendance status.
// Note: If attendance was never marked for a student, they are marked as ABSENT by default.
    public List<AttendanceRow> forSession(int sessionId) {
        return Db.query("SELECT st.id, st.reg_no, st.name, COALESCE(a.status, 'ABSENT') "
                + "FROM class_sessions cs "
                + "JOIN students st ON st.course_id = cs.course_id "
                + "LEFT JOIN attendance a ON a.session_id = cs.id AND a.student_id = st.id "
                + "WHERE cs.id = ? ORDER BY st.reg_no",
                rs
                -> new AttendanceRow(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)),
                sessionId);
    }

    /*  Insert, or update when the student was already marked for this session. */
    public void save(int sessionId, int studentId, String status) {
        Db.update("INSERT INTO attendance(session_id, student_id, status) VALUES(?,?,?) "
                + "ON DUPLICATE KEY UPDATE status = VALUES(status)",
                sessionId, studentId, status);
    }

    // Builds a dynamic search query for the report.
    // All filters are optional. If a filter value is null, 
    // it is ignored and won't affect the results.
    public List<ReportRow> report(
            Integer studentId, Integer subjectId, LocalDate from, LocalDate to, Integer lecturerId) {
        StringBuilder sql = new StringBuilder(
                "SELECT cs.session_date, st.reg_no, st.name, sb.name, cs.start_time, a.status "
                + "FROM attendance a "
                + "JOIN class_sessions cs ON cs.id = a.session_id "
                + "JOIN students st ON st.id = a.student_id "
                + "JOIN subjects sb ON sb.id = cs.subject_id WHERE 1=1");
        List<Object> p = new ArrayList<>();
        if (studentId != null) {
            sql.append(" AND st.id = ?");
            p.add(studentId);
        }
        if (subjectId != null) {
            sql.append(" AND sb.id = ?");
            p.add(subjectId);
        }
        if (from != null) {
            sql.append(" AND cs.session_date >= ?");
            p.add(from);
        }
        if (to != null) {
            sql.append(" AND cs.session_date <= ?");
            p.add(to);
        }
        if (lecturerId != null) {
            sql.append(" AND cs.lecturer_id = ?");
            p.add(lecturerId);
        }
        sql.append(" ORDER BY cs.session_date DESC, st.reg_no");
        return Db.query(sql.toString(),
                rs
                -> new ReportRow(rs.getDate(1).toString(), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getTime(5).toString().substring(0, 5), rs.getString(6)),
                p.toArray());
    }
}
