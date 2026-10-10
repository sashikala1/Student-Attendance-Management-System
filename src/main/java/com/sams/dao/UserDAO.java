package com.sams.dao;

import com.sams.model.User;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private static final Db.RowMapper<User> MAP = rs
            -> new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getObject(5) == null ? null : Integer.valueOf(rs.getInt(5)));
    private static final String SELECT
            = "SELECT id, username, password_hash, role, lecturer_id FROM users";

    public Optional<User> findByUsername(String username) {
        return Db.query(SELECT + " WHERE username = ?", MAP, username).stream().findFirst();
    }

    public List<User> findAll() {
        return Db.query(SELECT + " ORDER BY role, username", MAP);
    }

    public void insert(String username, String hash, String role, Integer lecturerId) {
        Db.insert("INSERT INTO users(username, password_hash, role, lecturer_id) VALUES(?,?,?,?)",
                username, hash, role, lecturerId);
    }

    public void delete(int id) {
        Db.update("DELETE FROM users WHERE id = ?", id);
    }
}
