package com.sams.ui;

import com.sams.model.Course;
import com.sams.model.Student;
import com.sams.service.CourseService;
import com.sams.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/*Student CRUD with live search.
 */
public class StudentPane extends Page {

    private final StudentService service = new StudentService();
    private final ObservableList<Student> data = FXCollections.observableArrayList();
    private final FilteredList<Student> filtered = new FilteredList<>(data, s -> true);
    private final TableView<Student> table = Ui.table();

    private final TextField name = Ui.text("Full name"), regNo = Ui.text("e.g. BSE2025010"),
            email = Ui.text("name@example.com"), phone = Ui.text("0771234567"), search = Ui.text("Search by name or reg. no...");
    private final ComboBox<Course> course = new ComboBox<>();

    public StudentPane() {
        getChildren().add(Ui.header("Student Management", "Register students, update their profiles or remove them"));

        course.setPromptText("Select course");
        course.setPrefWidth(220);
        course.getItems().setAll(new CourseService().courses());

        table.getColumns().add(Ui.col("Reg. No", Student::regNo));
        table.getColumns().add(Ui.col("Name", Student::name));
        table.getColumns().add(Ui.col("Course", Student::courseName));
        table.getColumns().add(Ui.col("Email", Student::email));
        table.getColumns().add(Ui.col("Phone", Student::phone));
        table.setItems(filtered);
        table.getSelectionModel().selectedItemProperty().addListener((o, old, s) -> {
            if (s == null) {
                return;
            }
            name.setText(s.name());
            regNo.setText(s.regNo());
            email.setText(s.email());
            phone.setText(s.phone());
            course.getItems().stream().filter(c -> c.id() == s.courseId()).findFirst().ifPresent(course::setValue);
        });

        // live search
        search.setPrefWidth(300);
        search.textProperty().addListener((o, old, text) -> {
            String q = text == null ? "" : text.trim().toLowerCase();
            filtered.setPredicate(s -> q.isEmpty() || s.name().toLowerCase().contains(q) || s.regNo().toLowerCase().contains(q));
        });

        VBox formCard = Ui.card("Student details",
                Ui.form(Ui.field("Name", name), Ui.field("Registration No", regNo), Ui.field("Course", course),
                        Ui.field("Email", email), Ui.field("Phone", phone)),
                Ui.buttons(
                        Ui.button("Add Student", "btn-primary", this::add),
                        Ui.button("Update", "btn-success", this::update),
                        Ui.button("Delete", "btn-danger", this::delete),
                        Ui.button("Clear", "btn-ghost", this::clear)));
        VBox listCard = Ui.card("Student roster", search, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);
        getChildren().addAll(formCard, listCard);
        load();
    }

    private void load() {
        data.setAll(service.list());
    }

    private Student fromForm(int id) {
        int courseId = course.getValue() == null ? 0 : course.getValue().id();
        return new Student(id, name.getText(), regNo.getText(), email.getText(), phone.getText(), courseId, null);
    }

    private void add() {
        service.save(fromForm(0));
        clear();
        load();
        showMessage("Success", "Student added successfully!");
    }

    private void update() {
        Student s = Ui.selected(table, "student");
        if (s == null) {
            return;
        }
        service.save(fromForm(s.id()));
        load();
        showMessage("Success", "Student updated successfully!");
    }

    private void delete() {
        Student s = Ui.selected(table, "student");
        if (s == null) {
            return;
        }

        if (!Ui.confirm("Delete student " + s.name() + "? Their attendance records will also be removed.")) {
            return;
        }

        service.delete(s.id());
        clear();
        load();
        showMessage("Success", "Student deleted successfully!");
    }

    private void clear() {
        name.clear();
        regNo.clear();
        email.clear();
        phone.clear();
        course.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    // --- Message box
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
