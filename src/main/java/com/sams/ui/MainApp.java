package com.sams.ui;

import com.sams.service.Session;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*Application entry point. Switches between the login screen and the main
 * window.
 */
public class MainApp extends Application {

    private static Stage stage;
    private static Scene scene;

    @Override
    public void start(Stage primary) {
        stage = primary;
        stage.setTitle("CJ Institute - Student Attendance Management System");
        stage.getIcons().add(Ui.logoImage());
        stage.setMinWidth(1050);
        stage.setMinHeight(680);
        showLogin();
        stage.show();
    }

    public static void showLogin() {
        Session.logout();
        setRoot(new LoginView(MainApp::showMain));
    }

    public static void showMain() {
        setRoot(new MainView(MainApp::showLogin));
    }

    private static void setRoot(Parent root) {
        if (scene == null) {
            scene = new Scene(root, 1200, 760);
            scene.getStylesheets().add(MainApp.class.getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
