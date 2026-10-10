package com.sams.dao;

import com.sams.util.AppException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// A central helper tool for database operations.
// Shared by all DAOs so you don't have to repeat code.
// Keeps database connections, parameter setting, and error handling all in one place.
public final class Db {
    //  CHANGE THESE TO MATCH YOUR MySQL INSTALLATION 

    private static final String URL
            = "jdbc:mysql://localhost:3306/"
            + "sams_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    // ===========================================================

    private Db() {
    }

    /**
     * Converts one ResultSet row into an object.
     */
    @FunctionalInterface
    public interface RowMapper<T> {

        T map(ResultSet rs) throws SQLException;
    }

    public static Connection get() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new AppException("Cannot connect to the database. Is MySQL running and is the password "
                    + "in Db.java correct?\n"
                    + e.getMessage(),
                    e);
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    /* SELECT: runs the query and maps every row. */
    public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    /* INSERT / UPDATE / DELETE: returns affected rows. */
    public static int update(String sql, Object... params) {
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AppException(
                    "Operation failed: duplicate value, or this record is still used by other data.", e);
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    /* INSERT that returns the generated primary key. */
    public static int insert(String sql, Object... params) {
        try (Connection c = get(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new AppException("Operation failed: duplicate value (code / reg. number / e-mail / "
                    + "username already exists).",
                    e);
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    /* SELECT COUNT(*) helper. */
    public static int count(String sql, Object... params) {
        List<Integer> r = query(sql, rs -> rs.getInt(1), params);
        return r.isEmpty() ? 0 : r.get(0);
    }
}
