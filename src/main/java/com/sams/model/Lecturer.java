package com.sams.model;

/* 'subjects' is a display string (e.g. "OOP101, DBM201") filled by the DAO for table views. */
public record Lecturer(int id, String name, String email, String phone, String subjects) {
  @Override
  public String toString() {
    return name;
  }
}
