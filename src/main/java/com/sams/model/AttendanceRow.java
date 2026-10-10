package com.sams.model;

/* One student's attendance for one class session. */
public class AttendanceRow {
  private final int studentId;
  private final String regNo;
  private final String name;
  private String status;

  public AttendanceRow(int studentId, String regNo, String name, String status) {
    this.studentId = studentId;
    this.regNo = regNo;
    this.name = name;
    this.status = status;
  }
  public int studentId() {
    return studentId;
  }
  public String regNo() {
    return regNo;
  }
  public String name() {
    return name;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
}
