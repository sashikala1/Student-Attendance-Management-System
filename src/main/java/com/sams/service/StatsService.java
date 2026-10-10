package com.sams.service;

import com.sams.dao.StatsDAO;

public class StatsService {

    private final StatsDAO dao = new StatsDAO();

    public int courses() {
        return dao.countCourses();
    }

    public int students() {
        return dao.countStudents();
    }

    public int lecturers() {
        return dao.countLecturers();
    }

    public int today() {
        return dao.countToday();
    }
}
