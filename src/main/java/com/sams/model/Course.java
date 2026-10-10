package com.sams.model;

public record Course(int id, String code, String name, int years) {
  @Override
  public String toString() {
    return code + " - " + name;
  }
}
