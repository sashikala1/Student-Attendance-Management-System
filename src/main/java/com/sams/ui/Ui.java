package com.sams.ui;

import com.sams.util.AppException;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.function.Function;

/*Small reusable UI helpers so every screen looks and behaves the same.
 */
public final class Ui {

    private Ui() {
    }

    // institute logo (src/main/resources/images/logo.png) 
    private static Image logoImage;

    public static Image logoImage() {
        if (logoImage == null) {
            logoImage = new Image(Ui.class.getResourceAsStream("/images/logo.png"));
        }
        return logoImage;
    }

    public static ImageView logo(double size) {
        ImageView v = new ImageView(logoImage());
        v.setFitWidth(size);
        v.setFitHeight(size);
        v.setPreserveRatio(true);
        v.setSmooth(true);
        return v;
    }

    //  dialogs
    public static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText("Something went wrong");
        a.showAndWait();
    }

    public static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    public static boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText("Please confirm");
        return a.showAndWait().filter(b -> b == ButtonType.YES).isPresent();
    }

    /*Runs an action; any AppException from the service layer is shown to the
     * user as a dialog.
     */
    public static void guard(Runnable action) {
        try {
            action.run();
        } catch (AppException ex) {
            error(ex.getMessage());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            error("Unexpected error: " + ex.getMessage());
        }
    }

    // controls
    public static Button button(String text, String styleClass, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().addAll("btn", styleClass);
        b.setOnAction(e -> guard(action));
        return b;
    }

    public static TextField text(String prompt) {
        TextField t = new TextField();
        t.setPromptText(prompt);
        t.setPrefWidth(200);
        return t;
    }

    /* Label on top of an input control.
     */
    public static VBox field(String label, Node input) {
        Label l = new Label(label);
        l.getStyleClass().add("field-label");
        VBox box = new VBox(5, l, input);
        return box;
    }

    public static FlowPane form(Node... fields) {
        FlowPane p = new FlowPane(16, 12, fields);
        p.setPadding(new Insets(4, 0, 4, 0));
        return p;
    }

    public static FlowPane buttons(Node... buttons) {
        return new FlowPane(10, 10, buttons);
    }

    public static VBox card(String title, Node... content) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        if (title != null) {
            Label t = new Label(title);
            t.getStyleClass().add("card-title");
            card.getChildren().add(t);
        }
        card.getChildren().addAll(content);
        return card;
    }

    public static VBox header(String title, String subtitle) {
        Label t = new Label(title);
        t.getStyleClass().add("page-title");
        Label s = new Label(subtitle);
        s.getStyleClass().add("page-sub");
        return new VBox(2, t, s);
    }

    // tables 
    @SuppressWarnings("deprecation")
    public static <T> TableView<T> table() {
        TableView<T> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.setPlaceholder(new Label("No records found"));
        return t;
    }

    public static <T> TableColumn<T, String> col(String title, Function<T, String> getter) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(d -> new SimpleStringProperty(getter.apply(d.getValue())));
        return c;
    }

  
    public static <T> TableColumn<T, String> statusCol(String title, Function<T, String> getter) {
        TableColumn<T, String> c = col(title, getter);
        c.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                getStyleClass().removeAll("st-present", "st-absent", "st-late");
                if (empty || s == null) {
                    setText(null);
                    return;
                }
                setText(s);
                getStyleClass().add("st-" + s.toLowerCase());
            }
        });
        return c;
    }

    /* Returns the selected row or throws a error.
     */
    public static <T> T selected(TableView<T> table, String what) {
        T s = table.getSelectionModel().getSelectedItem();
        if (s == null) {
            throw new AppException("Please select a " + what + " from the table first.");
        }
        return s;
    }
}
