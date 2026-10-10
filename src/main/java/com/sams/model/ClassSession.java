package com.sams.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClassSession(int id, int courseId, int subjectId, int lecturerId, LocalDate date,
        LocalTime start, LocalTime end, String courseName, String subjectName, String lecturerName) {

    @Override
    public String toString() {
        return date + " | " + start + "-" + end + " | " + subjectName + " (" + courseName + ")";
    }
}
