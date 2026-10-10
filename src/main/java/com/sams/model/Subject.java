package com.sams.model;

public record Subject(int id, String code, String name, int courseId, String courseName) {

    @Override
    public String toString() {
        return code + " - " + name;
    }
}
