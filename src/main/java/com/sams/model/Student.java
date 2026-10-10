package com.sams.model;

public record Student(int id, String name, String regNo, String email, String phone, int courseId,
    String courseName) {
  @Override
  public String toString() {
    return regNo + " - " + name;
  }
}
