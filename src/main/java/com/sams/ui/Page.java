package com.sams.ui;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/*Base class for every screen shown in the main content area.
 */
public abstract class Page extends VBox {

    protected Page() {
        setSpacing(18);
        setPadding(new Insets(26));
    }
}
