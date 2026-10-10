DROP DATABASE IF EXISTS sams_db;
CREATE DATABASE sams_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams_db;

-- ---------- COURSES ----------
CREATE TABLE courses (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    code           VARCHAR(20)  NOT NULL UNIQUE,
    name           VARCHAR(100) NOT NULL,
    duration_years INT          NOT NULL DEFAULT 3,
    CONSTRAINT chk_course_years CHECK (duration_years > 0)
) ENGINE=InnoDB;

-- ---------- SUBJECTS (each belongs to one course) ----------
CREATE TABLE subjects (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    code      VARCHAR(20)  NOT NULL UNIQUE,
    name      VARCHAR(100) NOT NULL,
    course_id INT          NOT NULL,
    CONSTRAINT fk_subject_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- STUDENTS (enrolled in one course) ----------
CREATE TABLE students (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    reg_no    VARCHAR(30)  NOT NULL UNIQUE,
    email     VARCHAR(100) NOT NULL,
    phone     VARCHAR(20)  NOT NULL,
    course_id INT          NOT NULL,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------- LECTURERS ----------
CREATE TABLE lecturers (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20)  NOT NULL
) ENGINE=InnoDB;

-- ---------- LECTURER <-> SUBJECT (many-to-many: assigned teaching subjects) ----------
CREATE TABLE lecturer_subjects (
    lecturer_id INT NOT NULL,
    subject_id  INT NOT NULL,
    PRIMARY KEY (lecturer_id, subject_id),
    CONSTRAINT fk_ls_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE CASCADE,
    CONSTRAINT fk_ls_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(id)  ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- USERS (login accounts, role based) ----------
CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash CHAR(64)    NOT NULL,                 -- SHA-256 hex
    role          ENUM('ADMIN','LECTURER') NOT NULL,
    lecturer_id   INT NULL,                             -- set for LECTURER accounts
    CONSTRAINT fk_user_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- CLASS SESSIONS ----------
CREATE TABLE class_sessions (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    course_id    INT  NOT NULL,
    subject_id   INT  NOT NULL,
    lecturer_id  INT  NOT NULL,
    session_date DATE NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    CONSTRAINT fk_cs_course   FOREIGN KEY (course_id)   REFERENCES courses(id)   ON DELETE RESTRICT,
    CONSTRAINT fk_cs_subject  FOREIGN KEY (subject_id)  REFERENCES subjects(id)  ON DELETE RESTRICT,
    CONSTRAINT fk_cs_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) ON DELETE RESTRICT,
    CONSTRAINT chk_cs_time CHECK (end_time > start_time),
    INDEX idx_cs_date (session_date)
) ENGINE=InnoDB;

-- ---------- ATTENDANCE (one row per student per session) ----------
CREATE TABLE attendance (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    student_id INT NOT NULL,
    status     ENUM('PRESENT','ABSENT','LATE') NOT NULL DEFAULT 'ABSENT',
    CONSTRAINT fk_att_session FOREIGN KEY (session_id) REFERENCES class_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES students(id)       ON DELETE CASCADE,
    CONSTRAINT uq_att UNIQUE (session_id, student_id)
) ENGINE=InnoDB;



-- Passwords: admin -> admin123 
INSERT INTO users (username, password_hash, role, lecturer_id) VALUES
 ('admin',     '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN',    NULL);


