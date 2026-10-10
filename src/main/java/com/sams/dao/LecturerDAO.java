package com.sams.dao;

import com.sams.model.Lecturer;
import java.util.List;

public class LecturerDAO {

    private static final Db.RowMapper<Lecturer> MAP = rs
            -> new Lecturer(
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));

    public List<Lecturer> findAll() {
        // GROUP_CONCAT joins all assigned subject codes into one display string
        return Db.query("SELECT l.id, l.name, l.email, l.phone, "
                + "       COALESCE(GROUP_CONCAT(s.code ORDER BY s.code SEPARATOR ', '), '') "
                + "FROM lecturers l "
                + "LEFT JOIN lecturer_subjects ls ON ls.lecturer_id = l.id "
                + "LEFT JOIN subjects s ON s.id = ls.subject_id "
                + "GROUP BY l.id, l.name, l.email, l.phone ORDER BY l.name",
                MAP);
    }

    /* Returns the new lecturer's id. */
    public int insert(Lecturer l) {
        return Db.insert(
                "INSERT INTO lecturers(name, email, phone) VALUES(?,?,?)", l.name(), l.email(), l.phone());
    }

    public void update(Lecturer l) {
        Db.update("UPDATE lecturers SET name=?, email=?, phone=? WHERE id=?", l.name(), l.email(),
                l.phone(), l.id());
    }

    public void delete(int id) {
        Db.update("DELETE FROM lecturers WHERE id=?", id);
    }

    public List<Integer> subjectIds(int lecturerId) {
        return Db.query("SELECT subject_id FROM lecturer_subjects WHERE lecturer_id=?",
                rs -> rs.getInt(1), lecturerId);
    }

    /* Replaces the lecturer's subject assignments. */
    public void setSubjects(int lecturerId, List<Integer> subjectIds) {
        Db.update("DELETE FROM lecturer_subjects WHERE lecturer_id=?", lecturerId);
        for (int sid : subjectIds) {
            Db.update(
                    "INSERT INTO lecturer_subjects(lecturer_id, subject_id) VALUES(?,?)", lecturerId, sid);
        }
    }
}
