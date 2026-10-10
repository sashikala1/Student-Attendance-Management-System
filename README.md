# SAMS - Student Attendance Management System

## 1. Project Overview
SAMS is a Java desktop application for tracking student attendance in an educational institution.
Two roles use it:
- **Admin** - manages courses, subjects, students, lecturers, class schedules, user accounts and views all reports.
- **Lecturer** - schedules his/her own classes, marks attendance and views reports of his/her own classes.

Business requirements covered: course management, student management, lecturer management,
class scheduling, attendance marking (Present / Absent / Late) and filterable attendance reports.

## 2. Technologies Used
| Area | Technology |
|------|-----------|
| Language | Java 21 |
| UI | JavaFX 21 (+ custom CSS) |
| Database | MySQL 8 |
| DB connectivity | JDBC (MySQL Connector/J 8.4) |
| Build tool | Maven |
| IDE | Apache NetBeans |
| Version control | Git / GitHub |

### Architecture (Layered / N-Tier)
```
com.sams.ui       -> Presentation Layer   (JavaFX screens, no SQL, no business rules)
com.sams.service  -> Service Layer        (business logic, validation, role checks)
com.sams.dao      -> Data Access Layer    (all JDBC / SQL)
MySQL (sams_db)   -> Data Layer
com.sams.model / com.sams.util -> shared models and helpers
```

## 3. Setup Instructions
1. Install **JDK 21**, **MySQL 8** and **Apache NetBeans** (with Maven support - included by default).
2. Open MySQL Workbench, open `database/sams_db.sql` and execute the whole script
   (creates database `sams_db`, all tables and sample data).
3. Open `src/main/java/com/sams/dao/Db.java` and set your MySQL `USER` and `PASSWORD`.
4. In NetBeans: **File > Open Project** and select the `SAMS` folder (it has a Maven icon).
5. Wait until the dependencies finish downloading, then press **Run (F6)**
   (or run `mvn javafx:run` in a terminal inside the project folder).

## 4. Login Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Lecturer | `lecturer1` | `lecturer123` |
| Lecturer | `lecturer2` | `lecturer123` |

## 5. Screenshots
_Add screenshots of the Login, Dashboard, Students, Attendance and Reports screens here._
Example: `![Login](screenshots/login.png)`
