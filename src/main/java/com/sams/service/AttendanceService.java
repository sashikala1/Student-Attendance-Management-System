package com.sams.service;

import com.sams.dao.AttendanceDAO;
import com.sams.dao.ClassSessionDAO;
import com.sams.model.*;
import com.sams.util.AppException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class AttendanceService {

    private final AttendanceDAO dao = new AttendanceDAO();
    private final ClassSessionDAO sessionDao = new ClassSessionDAO();
    private final ScheduleService schedule = new ScheduleService();

    public List<ClassSession> sessions() {
        return schedule.sessions();
    }

    public List<AttendanceRow> load(int sessionId) {
        checkAccess(sessionId);
        return dao.forSession(sessionId);
    }

    public void save(int sessionId, List<AttendanceRow> rows) {
        checkAccess(sessionId);
        for (AttendanceRow r : rows) {
            dao.save(sessionId, r.studentId(), r.getStatus());
        }
    }

    /**
     * Lecturers may only mark attendance for their own sessions.
     */
    private void checkAccess(int sessionId) {
        ClassSession cs = sessionDao.findById(sessionId)
                .orElseThrow(() -> new AppException("Class session not found."));
        User u = Session.user();
        if (!u.isAdmin() && !Objects.equals(u.lecturerId(), cs.lecturerId())) {
            throw new AppException("Access denied: this is not your class session.");
        }
    }

    public List<ReportRow> report(Student student, Subject subject, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new AppException("'From' date must not be after 'To' date.");
        }
        User u = Session.user();
        return dao.report(student == null ? null : student.id(), subject == null ? null : subject.id(),
                from, to, u.isAdmin() ? null : Session.lecturerId());
    }
}
