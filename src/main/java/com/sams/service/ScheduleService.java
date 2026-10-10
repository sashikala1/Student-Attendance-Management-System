package com.sams.service;

import com.sams.dao.ClassSessionDAO;
import com.sams.dao.LecturerDAO;
import com.sams.dao.SubjectDAO;
import com.sams.model.ClassSession;
import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.model.User;
import com.sams.util.AppException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/*Class scheduling rules. Admin sees everything; a lecturer
 * sees/creates/updates only his/her own sessions.
 */
public class ScheduleService {

    private final ClassSessionDAO sessions = new ClassSessionDAO();
    private final SubjectDAO subjects = new SubjectDAO();
    private final LecturerDAO lecturers = new LecturerDAO();

    public List<ClassSession> sessions() {
        User u = Session.user();
        return sessions.findAll(u.isAdmin() ? null : Session.lecturerId());
    }

    public List<Subject> subjectsForForm() {
        User u = Session.user();
        return u.isAdmin() ? subjects.findAll() : subjects.findByLecturer(Session.lecturerId());
    }

    public List<Lecturer> lecturersForForm() {
        User u = Session.user();
        if (u.isAdmin()) {
            return lecturers.findAll();
        }
        return lecturers.findAll().stream().filter(l -> l.id() == Session.lecturerId()).toList();
    }

    public void schedule(Subject subject, Lecturer lecturer, LocalDate date, String start, String end) {
        validateInput(subject, lecturer, date, start, end);

        LocalTime s = LocalTime.parse(start), e = LocalTime.parse(end);

        // Rule: no double-booking for the same lecturer
        if (sessions.countOverlap(lecturer.id(), date, s, e) > 0) {
            throw new AppException(lecturer.name() + " already has a class that overlaps this time slot.");
        }

        sessions.insert(new ClassSession(0, subject.courseId(), subject.id(), lecturer.id(), date, s, e, null, null, null));
    }

    public void update(int sessionId, Subject subject, Lecturer lecturer, LocalDate date, String start, String end) {
        validateInput(subject, lecturer, date, start, end);

        LocalTime s = LocalTime.parse(start), e = LocalTime.parse(end);

        // Overlap check excluding current session ID
        if (sessions.countOverlapExcluding(sessionId, lecturer.id(), date, s, e) > 0) {
            throw new AppException(lecturer.name() + " already has another class that overlaps this time slot.");
        }

        sessions.update(new ClassSession(sessionId, subject.courseId(), subject.id(), lecturer.id(), date, s, e, null, null, null));
    }

    private void validateInput(Subject subject, Lecturer lecturer, LocalDate date, String start, String end) {
        if (subject == null) {
            throw new AppException("Please select a subject.");
        }
        if (lecturer == null) {
            throw new AppException("Please select a lecturer.");
        }
        if (date == null) {
            throw new AppException("Please select a date.");
        }
        if (start == null || end == null) {
            throw new AppException("Please select start and end time.");
        }

        LocalTime s = LocalTime.parse(start), e = LocalTime.parse(end);
        if (!e.isAfter(s)) {
            throw new AppException("End time must be after the start time.");
        }

        User u = Session.user();
        if (!u.isAdmin() && !Objects.equals(u.lecturerId(), lecturer.id())) {
            throw new AppException("You can only schedule/update your own classes.");
        }

        // Rule: lecturer must be assigned to teach this subject
        boolean assigned = subjects.findByLecturer(lecturer.id()).stream().anyMatch(x -> x.id() == subject.id());
        if (!assigned) {
            throw new AppException(lecturer.name() + " is not assigned to teach " + subject.name() + ".");
        }
    }

    public void delete(ClassSession cs) {
        User u = Session.user();
        if (!u.isAdmin() && !Objects.equals(u.lecturerId(), cs.lecturerId())) {
            throw new AppException("You can only delete your own classes.");
        }
        sessions.delete(cs.id());
    }
}
