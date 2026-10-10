package com.sams.ui;

import com.sams.model.ClassSession;
import com.sams.model.User;
import com.sams.service.ScheduleService;
import com.sams.service.Session;
import com.sams.service.StatsService;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/*Home screen: summary cards (admin) & upcoming classes.*/
public class DashboardPane extends Page {

    public DashboardPane() {
        User u = Session.user();
        getChildren().add(Ui.header("Welcome, " + u.username(), "Here is an overview of your attendance system"));

        if (u.isAdmin()) {
            StatsService stats = new StatsService();
            HBox cards = new HBox(18,
                    statCard("Courses", stats.courses(), "stat-1"),
                    statCard("Students", stats.students(), "stat-2"),
                    statCard("Lecturers", stats.lecturers(), "stat-3"),
                    statCard("Classes Today", stats.today(), "stat-4"));
            getChildren().add(cards);
        }

        // upcoming sessions (today and later), earliest first
        List<ClassSession> upcoming = new ScheduleService().sessions().stream()
                .filter(s -> !s.date().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(ClassSession::date).thenComparing(ClassSession::start))
                .limit(8).toList();

        TableView<ClassSession> table = Ui.table();
        table.getColumns().add(Ui.col("Date", s -> s.date().toString()));
        table.getColumns().add(Ui.col("Time", s -> s.start() + " - " + s.end()));
        table.getColumns().add(Ui.col("Course", ClassSession::courseName));
        table.getColumns().add(Ui.col("Subject", ClassSession::subjectName));
        table.getColumns().add(Ui.col("Lecturer", ClassSession::lecturerName));
        table.getItems().setAll(upcoming);
        VBox card = Ui.card("Upcoming classes", table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(card, Priority.ALWAYS);
        getChildren().add(card);
    }

    private VBox statCard(String caption, int value, String style) {
        Label num = new Label(String.valueOf(value));
        num.getStyleClass().add("stat-number");
        Label cap = new Label(caption);
        cap.getStyleClass().add("stat-caption");
        VBox box = new VBox(4, num, cap);
        box.getStyleClass().addAll("stat-card", style);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }
}
