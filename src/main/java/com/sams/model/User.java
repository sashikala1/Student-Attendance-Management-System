package com.sams.model;

public record User(int id, String username, String passwordHash, String role, Integer lecturerId) {

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
