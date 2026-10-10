package com.sams.ui;

import com.sams.model.Lecturer;
import com.sams.model.User;
import com.sams.service.LecturerService;
import com.sams.service.UserService;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*User account management (admin only).
 */
public class UsersPane extends Page {

    private final UserService service = new UserService();
    private final TableView<User> table = Ui.table();
    private final TextField username = Ui.text("username");
    private final PasswordField password = new PasswordField();
    private final ComboBox<String> role = new ComboBox<>();
    private final ComboBox<Lecturer> lecturer = new ComboBox<>();
    private final Map<Integer, String> lecturerNames = new HashMap<>();

    public UsersPane() {
        getChildren().add(Ui.header("User Accounts", "Create or remove login accounts and assign roles"));

        List<Lecturer> lecturers = new LecturerService().list();
        lecturers.forEach(l -> lecturerNames.put(l.id(), l.name()));
        lecturer.getItems().setAll(lecturers);
        lecturer.setPromptText("Linked lecturer");
        lecturer.setPrefWidth(220);
        role.getItems().setAll("ADMIN", "LECTURER");
        role.setPromptText("Role");
        role.setPrefWidth(160);
        password.setPromptText("min 6 characters");
        password.setPrefWidth(200);
        // the lecturer drop-down only matters for the LECTURER role
        lecturer.disableProperty().bind(role.valueProperty().isNotEqualTo("LECTURER"));

        table.getColumns().add(Ui.col("Username", User::username));
        table.getColumns().add(Ui.col("Role", User::role));
        table.getColumns().add(Ui.col("Linked lecturer", u -> u.lecturerId() == null ? "-" : lecturerNames.getOrDefault(u.lecturerId(), "-")));

        VBox formCard = Ui.card("New account",
                Ui.form(Ui.field("Username", username), Ui.field("Password", password),
                        Ui.field("Role", role), Ui.field("Lecturer", lecturer)),
                Ui.buttons(Ui.button("Create Account", "btn-primary", this::create),
                        Ui.button("Delete Selected", "btn-danger", this::delete)));
        VBox listCard = Ui.card("Accounts", table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(listCard, Priority.ALWAYS);
        getChildren().addAll(formCard, listCard);
        load();
    }

    private void load() {
        table.getItems().setAll(service.list());
    }

    private void create() {
        Lecturer l = lecturer.getValue();
        service.create(username.getText(), password.getText(), role.getValue(), l == null ? null : l.id());
        username.clear();
        password.clear();
        role.setValue(null);
        lecturer.setValue(null);
        load();
        showMessage("Success", "User account created successfully!");
    }

    private void delete() {
        User u = Ui.selected(table, "user");
        if (u == null) {
            return;
        }

        if (!Ui.confirm("Delete account '" + u.username() + "'?")) {
            return;
        }

        service.delete(u);
        load();
        showMessage("Success", "User account deleted successfully!");
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
