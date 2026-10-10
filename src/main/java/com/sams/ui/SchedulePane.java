package com.sams.ui;

import com.sams.model.ClassSession;
import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.service.ScheduleService;
import com.sams.service.Session;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*Schedule class sessions (course is taken from the chosen subject).
 */
public class SchedulePane extends Page {

    private final ScheduleService service = new ScheduleService();
    private final TableView<ClassSession> table = Ui.table();
    private final ComboBox<Subject> subject = new ComboBox<>();
    private final ComboBox<Lecturer> lecturer = new ComboBox<>();
    private final DatePicker date = new DatePicker(LocalDate.now());
    private final ComboBox<String> start = new ComboBox<>(), end = new ComboBox<>();

    public SchedulePane() {
        getChildren().add(Ui.header("Class Scheduling", "Create and manage class sessions with subject, date, time slot and lecturer"));

        // 30-minute time slots from 07:00 to 19:30
        List<String> slots = new ArrayList<>();
        for (int h = 7; h <= 19; h++) {
            slots.add(String.format("%02d:00", h));
            slots.add(String.format("%02d:30", h));
        }
        start.getItems().setAll(slots);
        end.getItems().setAll(slots);
        start.setPromptText("Start");
        end.setPromptText("End");

        subject.setPromptText("Select subject");
        subject.setPrefWidth(280);
        lecturer.setPromptText("Select lecturer");
        lecturer.setPrefWidth(220);

        subject.getItems().setAll(service.subjectsForForm());
        lecturer.getItems().setAll(service.lecturersForForm());

        if (!Session.user().isAdmin() && !lecturer.getItems().isEmpty()) {   // a lecturer can only schedule for himself
            lecturer.setValue(lecturer.getItems().get(0));
            lecturer.setDisable(true);
        }

        table.getColumns().add(Ui.col("Date", s -> s.date().toString()));
        table.getColumns().add(Ui.col("Time", s -> s.start() + " - " + s.end()));
        table.getColumns().add(Ui.col("Course", ClassSession::courseName));
        table.getColumns().add(Ui.col("Subject", ClassSession::subjectName));
        table.getColumns().add(Ui.col("Lecturer", ClassSession::lecturerName));

        // When user selects a row in table, populate the form fields
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedSession) -> {
            if (selectedSession != null) {
                populateForm(selectedSession);
            }
        });

        VBox formCard = Ui.card("Manage Class Session",
                Ui.form(Ui.field("Subject", subject), Ui.field("Lecturer", lecturer), Ui.field("Date", date),
                        Ui.field("Start time", start), Ui.field("End time", end)),
                Ui.buttons(
                        Ui.button("Schedule Class", "btn-primary", this::schedule),
                        Ui.button("Update Selected", "btn-success", this::update),
                        Ui.button("Delete Selected", "btn-danger", this::delete),
                        Ui.button("Clear Form", "btn-secondary", this::clearForm)
                ));

        VBox listCard = Ui.card("Scheduled classes", table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);
        getChildren().addAll(formCard, listCard);
        load();
    }

    private void load() {
        table.getItems().setAll(service.sessions());
    }

    private void populateForm(ClassSession cs) {
        subject.getItems().stream()
                .filter(s -> s.id() == cs.subjectId())
                .findFirst()
                .ifPresent(subject::setValue);

        if (Session.user().isAdmin()) {
            lecturer.getItems().stream()
                    .filter(l -> l.id() == cs.lecturerId())
                    .findFirst()
                    .ifPresent(lecturer::setValue);
        }

        date.setValue(cs.date());
        start.setValue(cs.start().toString());
        end.setValue(cs.end().toString());
    }

    private void schedule() {
        service.schedule(subject.getValue(), lecturer.getValue(), date.getValue(), start.getValue(), end.getValue());
        clearForm();
        load();
        showMessage("Success", "Class scheduled successfully!");
    }

    private void update() {
        ClassSession s = Ui.selected(table, "class session to update");
        if (s == null) {
            return;
        }

        service.update(s.id(), subject.getValue(), lecturer.getValue(), date.getValue(), start.getValue(), end.getValue());
        clearForm();
        load();
        showMessage("Success", "Class session updated successfully!");
    }

    private void delete() {
        ClassSession s = Ui.selected(table, "class session");
        if (s == null) {
            return;
        }

        if (!Ui.confirm("Delete this class and its attendance records?")) {
            return;
        }

        service.delete(s);
        clearForm();
        load();
        showMessage("Success", "Class session deleted successfully!");
    }

    private void clearForm() {
        table.getSelectionModel().clearSelection();
        subject.setValue(null);
        if (Session.user().isAdmin()) {
            lecturer.setValue(null);
        }
        date.setValue(LocalDate.now());
        start.setValue(null);
        end.setValue(null);
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
