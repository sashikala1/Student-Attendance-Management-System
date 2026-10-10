package com.sams.ui;

import com.sams.service.AuthService;
import com.sams.util.AppException;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/* Login screen: gradient brand panel on the left, sign-in form on the right.
 */
public class LoginView extends HBox {

    private final AuthService auth = new AuthService();
    private final TextField username = new TextField();
    private final PasswordField password = new PasswordField();
    private final Label error = new Label();

    public LoginView(Runnable onSuccess) {
        // ----- left brand panel -----
        Label logo = new Label("SAMS");
        logo.getStyleClass().add("login-logo");
        Label instName = new Label("CJ Institute");
        instName.getStyleClass().add("login-inst");
        HBox instRow = new HBox(14, Ui.logo(64), instName);
        instRow.setAlignment(Pos.CENTER_LEFT);
        Label tagline = new Label("Student Attendance\nManagement System");
        tagline.getStyleClass().add("login-tagline");
        Label sub = new Label("Track classes, mark attendance and generate reports - all in one place.");
        sub.setWrapText(true);
        sub.getStyleClass().add("login-sub");
        VBox brand = new VBox(14, instRow, logo, tagline, sub);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.getStyleClass().add("login-brand");
        brand.setPrefWidth(520);

        // ----- right form panel -----
        Label title = new Label("Welcome back");
        title.getStyleClass().add("page-title");
        Label hint = new Label("Sign in to continue");
        hint.getStyleClass().add("page-sub");

        username.setPromptText("Enter your username");
        password.setPromptText("Enter your password");
        username.setPrefHeight(40);
        password.setPrefHeight(40);
        error.getStyleClass().add("error-label");
        error.setWrapText(true);

        Button login = new Button("Sign In");
        login.getStyleClass().addAll("btn", "btn-primary");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setPrefHeight(42);
        login.setDefaultButton(true);          // pressing ENTER triggers login
        login.setOnAction(e -> {
            try {
                auth.login(username.getText(), password.getText());
                onSuccess.run();
            } catch (AppException ex) {
                error.setText(ex.getMessage());
            }
        });

        VBox form = new VBox(14, title, hint,
                Ui.field("Username", username), Ui.field("Password", password), error, login);
        form.setMaxWidth(360);

        form.setAlignment(Pos.CENTER);

        StackPane right = new StackPane(form);
        right.getStyleClass().add("login-form");
        HBox.setHgrow(right, Priority.ALWAYS);
        getChildren().addAll(brand, right);
    }
}
