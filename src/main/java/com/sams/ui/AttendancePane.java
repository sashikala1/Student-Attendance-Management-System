package com.sams.ui;

import com.sams.model.AttendanceRow;
import com.sams.model.ClassSession;
import com.sams.service.AttendanceService;
import com.sams.util.AppException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.*;

/*Mark attendance: pick a class session, set each student's status, save.*/
public class AttendancePane extends Page {

    private final AttendanceService service = new AttendanceService();
    private final ComboBox<ClassSession> sessionBox = new ComboBox<>();
    private final TableView<AttendanceRow> table = Ui.table();
    private final Label summary = new Label();
    private ClassSession current;                                // session currently loaded in the table

    public AttendancePane() {
        getChildren().add(Ui.header("Attendance Marking", "Select a class, mark every student as Present, Absent or Late, then save"));

        sessionBox.setPromptText("Select a class session");
        sessionBox.setPrefWidth(560);
        sessionBox.getItems().setAll(service.sessions());

        // ----- table with an editable status column (drop-down per row) -----
        table.setEditable(true);
        table.getColumns().add(Ui.col("Reg. No", AttendanceRow::regNo));
        table.getColumns().add(Ui.col("Student", AttendanceRow::name));

        TableColumn<AttendanceRow, String> status = new TableColumn<>("Status (click to change)");
        status.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        status.setCellFactory(tc -> new ComboBoxTableCell<>(FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE")) {
            @Override
            public void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                getStyleClass().removeAll("st-present", "st-absent", "st-late");
                if (!empty && s != null) {
                    getStyleClass().add("st-" + s.toLowerCase());
                }
            }
        });
        status.setOnEditCommit(e -> {
            e.getRowValue().setStatus(e.getNewValue());
            updateSummary();
        });
        status.setEditable(true);
        table.getColumns().add(status);

        VBox pickCard = Ui.card("Class session",
                sessionBox,
                Ui.buttons(Ui.button("Load Students", "btn-primary", this::loadStudents)));
        VBox markCard = Ui.card("Students",
                Ui.buttons(
                        Ui.button("Mark All Present", "btn-ghost", () -> setAll("PRESENT")),
                        Ui.button("Mark All Absent", "btn-ghost", () -> setAll("ABSENT")),
                        Ui.button("Save Attendance", "btn-success", this::save)),
                summary, table);
        summary.getStyleClass().add("page-sub");
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(markCard, Priority.ALWAYS);
        getChildren().addAll(pickCard, markCard);
    }

    private void loadStudents() {
        ClassSession s = sessionBox.getValue();
        if (s == null) {
            throw new AppException("Please select a class session first.");
        }
        table.getItems().setAll(service.load(s.id()));
        current = s;
        if (table.getItems().isEmpty()) {
            summary.setText("No students are enrolled in this course.");
        } else {
            updateSummary();
        }
    }

    private void setAll(String st) {
        requireLoaded();
        table.getItems().forEach(r -> r.setStatus(st));
        table.refresh();
        updateSummary();
    }

    private void save() {
        requireLoaded();
        service.save(current.id(), table.getItems());
        showMessage("Success", "Attendance saved successfully for " + table.getItems().size() + " students.");
    }

    private void requireLoaded() {
        if (current == null || table.getItems().isEmpty()) {
            throw new AppException("Load a class session with students first.");
        }
    }

    private void updateSummary() {
        long p = table.getItems().stream().filter(r -> "PRESENT".equals(r.getStatus())).count();
        long l = table.getItems().stream().filter(r -> "LATE".equals(r.getStatus())).count();
        long a = table.getItems().stream().filter(r -> "ABSENT".equals(r.getStatus())).count();
        summary.setText("Present: " + p + "    Late: " + l + "    Absent: " + a + "    Total: " + table.getItems().size());
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
