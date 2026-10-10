package com.sams.ui;

import com.sams.model.User;
import com.sams.service.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

/*Main window: dark sidebar (menu depends on the user's ROLE) & content area.
 */
public class MainView extends BorderPane {

    private final StackPane content = new StackPane();
    private final ToggleGroup group = new ToggleGroup();
    private final VBox sidebar = new VBox(6);

    public MainView(Runnable onLogout) {
        User u = Session.user();

        Label brand = new Label("SAMS");
        brand.getStyleClass().add("brand");
        sidebar.getChildren().add(brand);

        // Role-based menu 
        if (u.isAdmin()) {
            nav("\uD83C\uDFE0   Dashboard", DashboardPane::new);
            nav("\uD83D\uDCDA   Courses", CoursePane::new);
            nav("\uD83C\uDF93   Students", StudentPane::new);
            nav("\uD83D\uDC68\u200D\uD83C\uDFEB   Lecturers", LecturerPane::new);
        } else {
            nav("\uD83C\uDFE0   Dashboard", DashboardPane::new);
        }
        nav("\uD83D\uDCC5   Class Schedule", SchedulePane::new);
        nav("\u2705   Attendance", AttendancePane::new);
        nav("\uD83D\uDCCA   Reports", ReportPane::new);
        if (u.isAdmin()) {
            nav("\uD83D\uDD10   User Accounts", UsersPane::new);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button logout = new Button("Logout");
        logout.getStyleClass().addAll("btn", "btn-danger");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setOnAction(e -> {
            if (Ui.confirm("Do you want to log out?")) {
                onLogout.run();
            }
        });
        sidebar.getChildren().addAll(spacer, logout);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        // ===== top bar: institute logo + name (left) | date + user chip (right) =====
        ImageView logo = Ui.logo(44);
        Label inst = new Label("CJ Institute");
        inst.getStyleClass().add("topbar-inst");
        Label instSub = new Label("Student Attendance Management System");
        instSub.getStyleClass().add("topbar-inst-sub");
        VBox instText = new VBox(1, inst, instSub);
        instText.setAlignment(Pos.CENTER_LEFT);
        HBox brandBox = new HBox(12, logo, instText);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Label date = new Label("\uD83D\uDCC5  " + LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")));
        date.getStyleClass().add("topbar-date");

        String name = u.username();
        Label avatar = new Label(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(36, 36);
        avatar.setPrefSize(36, 36);
        avatar.setAlignment(Pos.CENTER);
        Label userName = new Label(name);
        userName.getStyleClass().add("topbar-user");
        Label roleBadge = new Label(String.valueOf(u.role()));
        roleBadge.getStyleClass().addAll("role-badge", u.isAdmin() ? "role-admin" : "role-lecturer");
        HBox userChip = new HBox(10, avatar, userName, roleBadge);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.getStyleClass().add("user-chip");

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        HBox top = new HBox(18, brandBox, topSpacer, date, userChip);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(10, 26, 10, 22));
        top.getStyleClass().add("topbar");

        setLeft(sidebar);
        setTop(top);
        setCenter(content);

        ((ToggleButton) group.getToggles().get(0)).fire();   // open first page
    }

    /**
     * Adds a sidebar button; the page is created fresh each time so its data is
     * always up to date.
     */
    private void nav(String text, Supplier<Node> page) {
        ToggleButton b = new ToggleButton(text);
        b.setToggleGroup(group);
        b.getStyleClass().add("nav-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> {
            if (!b.isSelected()) {
                b.setSelected(true);        // keep the clicked item highlighted
            }
            Ui.guard(() -> content.getChildren().setAll(page.get()));
        });
        sidebar.getChildren().add(b);
    }
}
