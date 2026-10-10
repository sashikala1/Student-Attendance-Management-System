package com.sams.ui;

import com.sams.model.Lecturer;
import com.sams.model.Subject;
import com.sams.service.CourseService;
import com.sams.service.LecturerService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import java.util.*;

/*Lecturer CRUD & subject assignment (tick boxes) & optional login creation.
 */
public class LecturerPane extends Page {

    private final LecturerService service = new LecturerService();
    private final TableView<Lecturer> table = Ui.table();
    private final TextField name = Ui.text("Full name"), email = Ui.text("name@example.com"), phone = Ui.text("0711234567"),
            username = Ui.text("optional login username");
    private final PasswordField password = new PasswordField();
    private final ListView<Subject> subjectList = new ListView<>();
    private final Map<Integer, BooleanProperty> checks = new HashMap<>();   // subjectId -> ticked?

    public LecturerPane() {
        getChildren().add(Ui.header("Lecturer Management", "Maintain lecturer profiles and the subjects they teach"));

        password.setPromptText("min 6 characters");
        password.setPrefWidth(200);

        // subjects with check boxes
        List<Subject> all = new CourseService().allSubjects();
        for (Subject s : all) {
            checks.put(s.id(), new SimpleBooleanProperty(false));
        }
        subjectList.getItems().setAll(all);
        subjectList.setCellFactory(CheckBoxListCell.forListView(s -> checks.get(s.id())));
        subjectList.setPrefHeight(150);

        table.getColumns().add(Ui.col("Name", Lecturer::name));
        table.getColumns().add(Ui.col("Email", Lecturer::email));
        table.getColumns().add(Ui.col("Phone", Lecturer::phone));
        table.getColumns().add(Ui.col("Subjects", Lecturer::subjects));
        table.getSelectionModel().selectedItemProperty().addListener((o, old, l) -> {
            if (l == null) {
                return;
            }
            name.setText(l.name());
            email.setText(l.email());
            phone.setText(l.phone());
            Set<Integer> assigned = new HashSet<>(service.subjectIds(l.id()));
            checks.forEach((id, prop) -> prop.set(assigned.contains(id)));
        });

        VBox left = Ui.card("Lecturer details",
                Ui.form(Ui.field("Name", name), Ui.field("Email", email), Ui.field("Phone", phone)),
                Ui.form(Ui.field("Login username (new lecturers)", username), Ui.field("Login password", password)),
                Ui.buttons(
                        Ui.button("Add Lecturer", "btn-primary", this::add),
                        Ui.button("Update", "btn-success", this::update),
                        Ui.button("Delete", "btn-danger", this::delete),
                        Ui.button("Clear", "btn-ghost", this::clear)));
        VBox right = Ui.card("Assigned subjects (tick to assign)", subjectList);
        right.setPrefWidth(320);
        HBox top = new HBox(20, left, right);
        HBox.setHgrow(left, Priority.ALWAYS);

        VBox listCard = Ui.card("Lecturers", table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);
        getChildren().addAll(top, listCard);
        load();
    }

    private void load() {
        table.getItems().setAll(service.list());
    }

    private List<Integer> ticked() {
        return checks.entrySet().stream().filter(e -> e.getValue().get()).map(Map.Entry::getKey).toList();
    }

    private void add() {
        service.save(new Lecturer(0, name.getText(), email.getText(), phone.getText(), null),
                ticked(), username.getText(), password.getText());
        clear();
        load();
        showMessage("Success", "Lecturer added successfully!");
    }

    private void update() {
        Lecturer l = Ui.selected(table, "lecturer");
        if (l == null) {
            return; 
        }
        service.save(new Lecturer(l.id(), name.getText(), email.getText(), phone.getText(), null), ticked(), null, null);
        load();
        showMessage("Success", "Lecturer updated successfully!");
    }

    private void delete() {
        Lecturer l = Ui.selected(table, "lecturer");
        if (l == null) {
            return; 
        }
        if (!Ui.confirm("Delete lecturer " + l.name() + " and their login account?")) {
            return;
        }

        service.delete(l.id());
        clear();
        load();
        showMessage("Success", "Lecturer deleted successfully!");
    }

    private void clear() {
        name.clear();
        email.clear();
        phone.clear();
        username.clear();
        password.clear();
        checks.values().forEach(p -> p.set(false));
        table.getSelectionModel().clearSelection();
    }

    // --- Message box---
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
