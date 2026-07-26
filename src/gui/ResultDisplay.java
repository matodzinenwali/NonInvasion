package gui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class ResultDisplay {
    
    // Custom cell factory for match results
    public static Callback<ListView<String>, ListCell<String>> getCellFactory() {
        return listView -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("No matches found")) {
                        setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
                    } else {
                        setStyle("-fx-text-fill: #2c3e50;");
                    }
                }
            }
        };
    }
}