import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    // UI components
    private TextField databasePathField, queryPathField, topKField;
    private ImageView dbImageView, queryImageView, pathImageView;
    private ListView<String> matchListView;
    private Label matchStatusLabel, startPointLabel, endPointLabel, pathStatusLabel, statusLabel;
    private ProgressBar progressBar;
    private Button identifyButton, findPathButton;

    // State
    private File currentDbFolder, currentQueryFile;
    private Image currentPathImage;
    private double clickX, clickY;
    private boolean isStartPointSelected = false;

    @Override
    public void start(Stage primaryStage) {
        //  Build the UI 
        TabPane tabPane = new TabPane();

        //  Tab 1: Animal Identification 
        Tab idTab = new Tab("Animal Identification");
        idTab.setClosable(false);

        GridPane idGrid = new GridPane();
        idGrid.setHgap(10);
        idGrid.setVgap(10);
        idGrid.setPadding(new Insets(20));

        // Database path
        Label dbLabel = new Label("Database:");
        dbLabel.setStyle("-fx-font-weight: bold;");
        databasePathField = new TextField();
        databasePathField.setEditable(false);
        Button loadDbButton = new Button("Browse Database...");
        loadDbButton.setOnAction(e -> handleLoadDatabase());

        idGrid.add(dbLabel, 0, 0);
        idGrid.add(databasePathField, 1, 0);
        idGrid.add(loadDbButton, 2, 0);

        // Query path
        Label queryLabel = new Label("Query:");
        queryLabel.setStyle("-fx-font-weight: bold;");
        queryPathField = new TextField();
        queryPathField.setEditable(false);
        Button loadQueryButton = new Button("Load Query...");
        loadQueryButton.setOnAction(e -> handleLoadQuery());
        Button clearQueryButton = new Button("Clear");
        clearQueryButton.setOnAction(e -> handleClearQuery());

        HBox queryButtons = new HBox(5, loadQueryButton, clearQueryButton);
        idGrid.add(queryLabel, 0, 1);
        idGrid.add(queryPathField, 1, 1);
        idGrid.add(queryButtons, 2, 1);

        // Top-K
        Label topKLabel = new Label("Top K:");
        topKLabel.setStyle("-fx-font-weight: bold;");
        topKField = new TextField("3");
        topKField.setMaxWidth(60);
        identifyButton = new Button("Identify");
        identifyButton.setDisable(true);
        identifyButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        identifyButton.setOnAction(e -> handleIdentify());

        HBox topKBox = new HBox(10, topKLabel, topKField, identifyButton);
        idGrid.add(topKBox, 0, 2, 3, 1);

        // Image previews
        VBox dbImageBox = new VBox(5);
        dbImageBox.getChildren().addAll(
            new Label("Database Image (sample)"),
            (dbImageView = new ImageView())
        );
        dbImageView.setFitHeight(150);
        dbImageView.setFitWidth(150);
        dbImageView.setPreserveRatio(true);
        dbImageView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1;");

        VBox queryImageBox = new VBox(5);
        queryImageBox.getChildren().addAll(
            new Label("Query Image"),
            (queryImageView = new ImageView())
        );
        queryImageView.setFitHeight(200);
        queryImageView.setFitWidth(200);
        queryImageView.setPreserveRatio(true);
        queryImageView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1;");

        HBox imageBox = new HBox(20, dbImageBox, queryImageBox);
        idGrid.add(imageBox, 0, 3, 3, 1);

        // Results
        VBox resultsBox = new VBox(5);
        resultsBox.getChildren().addAll(
            new Label("Matches"),
            (matchStatusLabel = new Label("No matches yet")),
            (matchListView = new ListView<>())
        );
        matchStatusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        matchListView.setPrefHeight(150);
        matchListView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1;");
        idGrid.add(resultsBox, 0, 4, 3, 1);

        idTab.setContent(idGrid);

        //Tab 2: Pathfinding
        Tab pathTab = new Tab("Drone Path Planning");
        pathTab.setClosable(false);

        VBox pathRoot = new VBox();

        HBox pathTop = new HBox(10);
        Button loadPathButton = new Button("Load Image...");
        loadPathButton.setOnAction(e -> handleLoadPathImage());
        Button clearPathButton = new Button("Clear");
        clearPathButton.setOnAction(e -> handleClearPath());
        Label pathTip = new Label("Click on image to set start & end points.");
        pathTip.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        pathTop.getChildren().addAll(loadPathButton, clearPathButton, pathTip);

        pathImageView = new ImageView();
        pathImageView.setFitHeight(400);
        pathImageView.setFitWidth(500);
        pathImageView.setPreserveRatio(true);
        pathImageView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2; -fx-cursor: crosshair;");
        pathImageView.setOnMouseClicked(event -> {
            if (currentPathImage == null) return;
            double imageX = event.getX();
            double imageY = event.getY();
            double imgWidth = pathImageView.getImage().getWidth();
            double imgHeight = pathImageView.getImage().getHeight();
            clickX = (imageX / pathImageView.getFitWidth()) * imgWidth;
            clickY = (imageY / pathImageView.getFitHeight()) * imgHeight;

            if (!isStartPointSelected) {
                startPointLabel.setText("Start: (" + String.format("%.0f", clickX) + ", " + String.format("%.0f", clickY) + ")");
                isStartPointSelected = true;
                findPathButton.setDisable(false);
                statusLabel.setText("Select end point on the image");
            } else {
                endPointLabel.setText("End: (" + String.format("%.0f", clickX) + ", " + String.format("%.0f", clickY) + ")");
                isStartPointSelected = false;
                statusLabel.setText("Points selected – click Find Path");
            }
        });

        // Path controls
        GridPane controlsGrid = new GridPane();
        controlsGrid.setHgap(10);
        controlsGrid.setVgap(10);

        Label startLabel = new Label("Start:");
        startLabel.setStyle("-fx-font-weight: bold;");
        startPointLabel = new Label("(click image to set)");
        startPointLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label endLabel = new Label("End:");
        endLabel.setStyle("-fx-font-weight: bold;");
        endPointLabel = new Label("(click image to set)");
        endPointLabel.setStyle("-fx-text-fill: #2c3e50;");

        findPathButton = new Button("Find Shortest Path");
        findPathButton.setDisable(true);
        findPathButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
        findPathButton.setOnAction(e -> handleFindPath());

        pathStatusLabel = new Label("");
        pathStatusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");

        controlsGrid.add(startLabel, 0, 0);
        controlsGrid.add(startPointLabel, 1, 0);
        controlsGrid.add(endLabel, 0, 1);
        controlsGrid.add(endPointLabel, 1, 1);
        controlsGrid.add(findPathButton, 0, 2);
        controlsGrid.add(pathStatusLabel, 1, 2);

        // Path results
        VBox pathResultsBox = new VBox(5);
        pathResultsBox.getChildren().addAll(
            new Label("Path Details"),
            (pathListView = new ListView<>())
        );
        pathListView.setPrefHeight(150);
        pathListView.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1;");

        pathRoot.getChildren().addAll(pathTop, pathImageView, controlsGrid, pathResultsBox);
        pathTab.setContent(pathRoot);

        tabPane.getTabs().addAll(idTab, pathTab);

        // Status Bar 
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(100);

        statusBar.getChildren().addAll(statusLabel, spacer, progressBar);

        //Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        mainLayout.setBottom(statusBar);

        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("WildTrack – Wildlife Monitoring System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    //  Event Handlers (placeholder implementations) 

    private void handleLoadDatabase() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Animal Database Folder");
        File dir = chooser.showDialog(null);
        if (dir != null) {
            currentDbFolder = dir;
            databasePathField.setText(dir.getAbsolutePath());
            statusLabel.setText("Loading database from: " + dir.getName());
            progressBar.setProgress(0.5);
            // In a real implementation, you'd load images and build the tree
            statusLabel.setText("Database loaded (stub)");
            progressBar.setProgress(1.0);
            identifyButton.setDisable(false);
            showAlert("Success", "Database loaded (stub).");
        }
    }

    private void handleLoadQuery() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Animal Image to Identify");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            currentQueryFile = file;
            queryPathField.setText(file.getAbsolutePath());
            try {
                Image image = new Image(file.toURI().toString());
                queryImageView.setImage(image);
                statusLabel.setText("Query loaded: " + file.getName());
                identifyButton.setDisable(false);
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    private void handleIdentify() {
        if (currentQueryFile == null) {
            showAlert("No Query", "Please load a query image first.");
            return;
        }
        statusLabel.setText("Identifying animal...");
        progressBar.setProgress(0.5);
        identifyButton.setDisable(true);

        // Simulate matching (placeholder)
        matchListView.getItems().clear();
        int topK = 3;
        try { topK = Integer.parseInt(topKField.getText()); } catch (NumberFormatException ignored) {}
        for (int i = 0; i < topK; i++) {
            matchListView.getItems().add((i+1) + ". Animal_XYZ_" + (char)('A'+i) + "  (85." + (i*5) + "%)");
        }
        matchStatusLabel.setText("Top " + topK + " matches found (stub)");
        statusLabel.setText("Identification complete (stub)");
        progressBar.setProgress(1.0);
        identifyButton.setDisable(false);
    }

    private void handleClearQuery() {
        queryImageView.setImage(null);
        queryPathField.clear();
        matchListView.getItems().clear();
        matchStatusLabel.setText("No matches yet");
        currentQueryFile = null;
        identifyButton.setDisable(true);
        statusLabel.setText("Query cleared");
    }

    private void handleLoadPathImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image for Pathfinding");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            try {
                currentPathImage = new Image(file.toURI().toString());
                pathImageView.setImage(currentPathImage);
                isStartPointSelected = false;
                startPointLabel.setText("Start: (click image to set)");
                endPointLabel.setText("End: (click image to set)");
                findPathButton.setDisable(true);
                pathListView.getItems().clear();
                statusLabel.setText("Pathfinding image loaded: " + file.getName());
                showAlert("Pathfinding Ready", "Click on the image to set start and end points.");
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    private void handleFindPath() {
        if (currentPathImage == null) {
            showAlert("No Image", "Please load an image for pathfinding.");
            return;
        }
        statusLabel.setText("Computing shortest path...");
        progressBar.setProgress(0.5);
        findPathButton.setDisable(true);

        // Simulate pathfinding (placeholder)
        pathListView.getItems().clear();
        pathListView.getItems().add("Start: (" + String.format("%.0f", clickX) + ", " + String.format("%.0f", clickY) + ")");
        pathListView.getItems().add("End: (" + String.format("%.0f", clickX+10) + ", " + String.format("%.0f", clickY+5) + ")");
        pathListView.getItems().add("Path length: 42.0 units");
        pathListView.getItems().add("Nodes visited: 156");
        statusLabel.setText("Pathfinding complete (stub)");
        progressBar.setProgress(1.0);
        findPathButton.setDisable(false);
        showAlert("Pathfinding Demo", "Full pathfinding implementation coming soon!");
    }

    private void handleClearPath() {
        pathImageView.setImage(null);
        pathListView.getItems().clear();
        startPointLabel.setText("Start: (click image to set)");
        endPointLabel.setText("End: (click image to set)");
        findPathButton.setDisable(true);
        currentPathImage = null;
        isStartPointSelected = false;
        statusLabel.setText("Pathfinding cleared");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ListView for path results
    private ListView<String> pathListView;

    public static void main(String[] args) {
        launch(args);
    }
}