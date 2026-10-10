package com.sams.ui;

import com.sams.model.ReportRow;
import com.sams.model.Student;
import com.sams.model.Subject;
import com.sams.service.AttendanceService;
import com.sams.service.ScheduleService;
import com.sams.service.StudentService;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

/*Attendance report with optional filters student, subject, date range.
 */
public class ReportPane extends Page {

    private final AttendanceService service = new AttendanceService();
    private final TableView<ReportRow> table = Ui.table();
    private final ComboBox<Student> student = new ComboBox<>();
    private final ComboBox<Subject> subject = new ComboBox<>();
    private final DatePicker from = new DatePicker(), to = new DatePicker();
    private final Label summary = new Label();

    public ReportPane() {
        getChildren().add(Ui.header("Attendance Reports", "Filter by student, subject or date range (leave a filter empty to ignore it)"));

        student.setPromptText("All students");
        student.setPrefWidth(260);
        subject.setPromptText("All subjects");
        subject.setPrefWidth(260);
        from.setPromptText("From date");
        to.setPromptText("To date");
        student.getItems().setAll(new StudentService().list());
        subject.getItems().setAll(new ScheduleService().subjectsForForm());

        table.getColumns().add(Ui.col("Date", ReportRow::date));
        table.getColumns().add(Ui.col("Time", ReportRow::time));
        table.getColumns().add(Ui.col("Reg. No", ReportRow::regNo));
        table.getColumns().add(Ui.col("Student", ReportRow::student));
        table.getColumns().add(Ui.col("Subject", ReportRow::subject));
        table.getColumns().add(Ui.statusCol("Status", ReportRow::status));

        summary.getStyleClass().add("page-sub");
        VBox filterCard = Ui.card("Filters",
                Ui.form(Ui.field("Student", student), Ui.field("Subject", subject),
                        Ui.field("From", from), Ui.field("To", to)),
                Ui.buttons(Ui.button("Generate Report", "btn-primary", this::generate),
                        Ui.button("Clear Filters", "btn-ghost", this::clear)));
        VBox resultCard = Ui.card("Results", summary, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(resultCard, Priority.ALWAYS);
        getChildren().addAll(filterCard, resultCard);
        generate();                                           // show everything on first open
    }

    private void generate() {
        List<ReportRow> rows = service.report(student.getValue(), subject.getValue(), from.getValue(), to.getValue());
        table.getItems().setAll(rows);
        long present = rows.stream().filter(r -> "PRESENT".equals(r.status())).count();
        long late = rows.stream().filter(r -> "LATE".equals(r.status())).count();
        long absent = rows.stream().filter(r -> "ABSENT".equals(r.status())).count();
        double rate = rows.isEmpty() ? 0 : (present + late) * 100.0 / rows.size();
        summary.setText(String.format("Records: %d    Present: %d    Late: %d    Absent: %d    Attendance rate: %.1f%%",
                rows.size(), present, late, absent, rate));
    }

    private void clear() {
        student.setValue(null);
        subject.setValue(null);
        from.setValue(null);
        to.setValue(null);
        generate();
    }
}
