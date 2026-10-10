package com.sams.ui;

import com.sams.model.Course;
import com.sams.model.Subject;
import com.sams.service.CourseService;
import com.sams.util.Validator;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/*Course CRUD (left) + management of the subjects of the selected course
 * (right).
 */
public class CoursePane extends Page {

    private final CourseService service = new CourseService();
    private final TableView<Course> courseTable = Ui.table();
    private final TableView<Subject> subjectTable = Ui.table();
    private final TextField cCode = Ui.text("e.g. BSE"), cName = Ui.text("Course name"), cYears = Ui.text("Years");
    private final TextField sCode = Ui.text("e.g. OOP101"), sName = Ui.text("Subject name");
    private final Label subjectTitle = new Label("Subjects");

    public CoursePane() {
        getChildren().add(Ui.header("Course Management", "Create courses and manage the subjects offered under each course"));

        // ---------- courses card ----------
        courseTable.getColumns().add(Ui.col("Code", Course::code));
        courseTable.getColumns().add(Ui.col("Course name", Course::name));
        courseTable.getColumns().add(Ui.col("Years", c -> String.valueOf(c.years())));
        courseTable.getSelectionModel().selectedItemProperty().addListener((o, old, c) -> {
            if (c == null) {
                return;
            }
            cCode.setText(c.code());
            cName.setText(c.name());
            cYears.setText(String.valueOf(c.years()));
            subjectTitle.setText("Subjects of " + c.code());
            loadSubjects(c.id());
        });
        VBox courseCard = Ui.card("Courses",
                Ui.form(Ui.field("Code", cCode), Ui.field("Name", cName), Ui.field("Duration (years)", cYears)),
                Ui.buttons(
                        Ui.button("Add", "btn-primary", this::addCourse),
                        Ui.button("Update", "btn-success", this::updateCourse),
                        Ui.button("Delete", "btn-danger", this::deleteCourse),
                        Ui.button("Clear", "btn-ghost", this::clearCourse)),
                courseTable);
        VBox.setVgrow(courseTable, Priority.ALWAYS);

        // ---------- subjects card ----------
        subjectTable.getColumns().add(Ui.col("Code", Subject::code));
        subjectTable.getColumns().add(Ui.col("Subject name", Subject::name));
        subjectTable.getSelectionModel().selectedItemProperty().addListener((o, old, s) -> {
            if (s != null) {
                sCode.setText(s.code());
                sName.setText(s.name());
            }
        });
        subjectTitle.getStyleClass().add("card-title");
        VBox subjectCard = Ui.card(null, subjectTitle,
                Ui.form(Ui.field("Code", sCode), Ui.field("Name", sName)),
                Ui.buttons(
                        Ui.button("Add", "btn-primary", this::addSubject),
                        Ui.button("Update", "btn-success", this::updateSubject),
                        Ui.button("Delete", "btn-danger", this::deleteSubject)),
                subjectTable);
        VBox.setVgrow(subjectTable, Priority.ALWAYS);

        HBox row = new HBox(20, courseCard, subjectCard);
        HBox.setHgrow(courseCard, Priority.ALWAYS);
        HBox.setHgrow(subjectCard, Priority.ALWAYS);
        courseCard.setPrefWidth(10);
        subjectCard.setPrefWidth(10);   // equal widths
        VBox.setVgrow(row, Priority.ALWAYS);
        getChildren().add(row);
        loadCourses();
    }

    // ----- course actions -----
    private void loadCourses() {
        courseTable.getItems().setAll(service.courses());
    }

    private void addCourse() {
        service.saveCourse(new Course(0, cCode.getText(), cName.getText(), Validator.integer(cYears.getText(), "Duration")));
        clearCourse();
        loadCourses();
        showMessage("Success", "Course added successfully!");
    }

    private void updateCourse() {
        Course sel = Ui.selected(courseTable, "course");
        if (sel == null) {
            return; // 
        }
        service.saveCourse(new Course(sel.id(), cCode.getText(), cName.getText(), Validator.integer(cYears.getText(), "Duration")));
        loadCourses();
        showMessage("Success", "Course updated successfully!");
    }

    private void deleteCourse() {
        Course sel = Ui.selected(courseTable, "course");
        if (sel == null) {
            return;
        }

        if (!Ui.confirm("Delete course " + sel.code() + " and all of its subjects?")) {
            return;
        }

        service.deleteCourse(sel.id());
        clearCourse();
        loadCourses();
        showMessage("Success", "Course deleted successfully!");
    }

    private void clearCourse() {
        cCode.clear();
        cName.clear();
        cYears.clear();
        courseTable.getSelectionModel().clearSelection();
        subjectTable.getItems().clear();
        subjectTitle.setText("Subjects");
    }

    // ----- subject actions -----
    private void loadSubjects(int courseId) {
        subjectTable.getItems().setAll(service.subjects(courseId));
    }

    private void addSubject() {
        Course course = Ui.selected(courseTable, "course (to add the subject to)");
        if (course == null) {
            return;
        }

        service.saveSubject(new Subject(0, sCode.getText(), sName.getText(), course.id(), null));
        sCode.clear();
        sName.clear();
        loadSubjects(course.id());
        showMessage("Success", "Subject added successfully!");
    }

    private void updateSubject() {
        Course course = Ui.selected(courseTable, "course");
        if (course == null) {
            return;
        }

        Subject sel = Ui.selected(subjectTable, "subject");
        if (sel == null) {
            return;
        }

        service.saveSubject(new Subject(sel.id(), sCode.getText(), sName.getText(), course.id(), null));
        loadSubjects(course.id());
        showMessage("Success", "Subject updated successfully!");
    }

    private void deleteSubject() {
        Course course = Ui.selected(courseTable, "course");
        if (course == null) {
            return;
        }

        Subject sel = Ui.selected(subjectTable, "subject");
        if (sel == null) {
            return;
        }

        if (!Ui.confirm("Delete subject " + sel.code() + "?")) {
            return;
        }

        service.deleteSubject(sel.id());
        sCode.clear();
        sName.clear();
        loadSubjects(course.id());
        showMessage("Success", "Subject deleted successfully!");
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
