package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import similarity.AnimalDatabase;
import similarity.SimilaritySearch;

import java.io.File;
import java.util.List;

import domain.Animal;
import image.FeatureExctractor;
import image.ImageLoader;

public class MainController{
	
	//FXMM fields
	@FXML private TabPane mainTabPane;
	
	//Identification Tab
	@FXML private Button loadDbButton;
	@FXML private Button loadQueryButton;
	@FXML private Button identifyButton;
	@FXML private Button dbImageView;
	@FXML private ImageView queryImageView;
	@FXML private ListView<String> matchListView;
	@FXML private Label matchStatusLabel;
	@FXML private TextField databasePathField;
	@FXML private TextField queryPathField;
	@FXML private TextField topKField;
	
	//Path-finding Tab
    @FXML private Button loadPathImageButton;
    @FXML private Button findPathButton;
    @FXML private ImageView pathImageView;
    @FXML private ListView<String> pathListView;
    @FXML private Label pathStatusLabel;
    @FXML private Label startPointLabel;
    @FXML private Label endPointLabel;
    
    //Status bar
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;
    
    //Backend Service
    private AnimalDatabase animalDatabase;
    private SimilaritySearch similaritySearch;
    private FeatureExctractor featureExtractor;
    private ImageLoader imageLoader;
    
    //State
    private File currentDbFolder;
    private File currentQueryFile;
    private Image currentPathImage;
    private double clickX, clickY;
    private boolean isStartPointSelected = false;
    
    //Initialization
    @FXML
    public void initialize() {
    	//initialize services
    	imageLoader = new ImageLoader();
    	featureExtractor = new FeatureExctractor();
    	animalDatabase = new AnimalDatabase(featureExtractor);
    	similaritySearch = new SimilaritySearch(animalDatabase);
    	
    	//set default top K
    	topKField.setText("3");
    	
        // Disable buttons until data is loaded
        identifyButton.setDisable(true);
        findPathButton.setDisable(true);
        
        statusLabel.setText("Ready");
        progressBar.setProgress(0);
        
        // Setup click handler for pathfinding image
        pathImageView.setOnMouseClicked(event -> {
            if (currentPathImage == null) return;
            
            double imageX = event.getX();
            double imageY = event.getY();
            double imageWidth = pathImageView.getImage().getWidth();
            double imageHeight = pathImageView.getImage().getHeight();
            
            // Convert to image coordinates
            clickX = (imageX / pathImageView.getFitWidth()) * imageWidth;
            clickY = (imageY / pathImageView.getFitHeight()) * imageHeight;
            
            if (!isStartPointSelected) {
                startPointLabel.setText("Start: (" + String.format("%.0f", clickX) + ", " + 
                                        String.format("%.0f", clickY) + ")");
                isStartPointSelected = true;
                findPathButton.setDisable(false);
                statusLabel.setText("Select end point on the image");
            } else {
                endPointLabel.setText("End: (" + String.format("%.0f", clickX) + ", " + 
                                      String.format("%.0f", clickY) + ")");
                isStartPointSelected = false;
                statusLabel.setText("Points selected – click Find Path");
                findPathButton.setDisable(false);
            }
        });
    }
    
   // ===== Event Handlers =====
    
    @FXML
    private void handleLoadDatabase() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Animal Database Folder");
        File selectedDir = directoryChooser.showDialog(null);
        
        if (selectedDir != null) {
            currentDbFolder = selectedDir;
            databasePathField.setText(selectedDir.getAbsolutePath());
            statusLabel.setText("Loading database from: " + selectedDir.getName());
            progressBar.setProgress(0.1);
            
            try {
                // Load all images from the folder
                File[] imageFiles = selectedDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".png") ||
                    name.toLowerCase().endsWith(".jpeg"));
                
                if (imageFiles == null || imageFiles.length == 0) {
                    showAlert("No images found", "The selected folder contains no supported image files.");
                    statusLabel.setText("No images found in database folder");
                    progressBar.setProgress(0);
                    return;
                }
                
                // Build the database
                int total = imageFiles.length;
                int loaded = 0;
                for (File imageFile : imageFiles) {
                    String animalId = imageFile.getName().substring(0, imageFile.getName().lastIndexOf('.'));
                    animalDatabase.addAnimal(animalId, imageFile);
                    loaded++;
                    progressBar.setProgress(0.1 + 0.8 * ((double) loaded / total));
                }
                
                int indexed = animalDatabase.getSize();
                statusLabel.setText("Database loaded: " + indexed + " animals indexed");
                progressBar.setProgress(1.0);
                
                // Enable identification
                identifyButton.setDisable(false);
                
                showAlert("Success", "Loaded " + indexed + " animals into the database.");
                
            } catch (Exception e) {
                showAlert("Error", "Failed to load database: " + e.getMessage());
                statusLabel.setText("Error loading database");
                progressBar.setProgress(0);
                e.printStackTrace();
            }
        }
    }
    
    @FXML  private void handleLoadQuery() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Animal Image to Identify");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            currentQueryFile = selectedFile;
            queryPathField.setText(selectedFile.getAbsolutePath());
            
            try {
                Image image = imageLoader.loadImage(selectedFile);
                queryImageView.setImage(image);
                statusLabel.setText("Query loaded: " + selectedFile.getName());
                identifyButton.setDisable(false);
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
                statusLabel.setText("Error loading query");
            }
        }
    }
    
    @FXML
    private void handleIdentify() {
        if (currentQueryFile == null) {
            showAlert("No Query", "Please load a query image first.");
            return;
        }
        
        if (animalDatabase.getSize() == 0) {
            showAlert("No Database", "Please load a database first.");
            return;
        }
        
        statusLabel.setText("Identifying animal...");
        progressBar.setProgress(0.5);
        identifyButton.setDisable(true);
        
        try {
            // Get top-K
            int topK = 3;
            try {
                topK = Integer.parseInt(topKField.getText());
                if (topK < 1) topK = 1;
                if (topK > 20) topK = 20;
            } catch (NumberFormatException e) {
                topK = 3;
            }
            
            // Perform identification
            List<String> matches = similaritySearch.findMatches(currentQueryFile, topK);
            
            // Display results
            matchListView.getItems().clear();
            if (matches.isEmpty()) {
                matchListView.getItems().add("No matches found");
                matchStatusLabel.setText("No similar animals found");
            } else {
                for (int i = 0; i < matches.size(); i++) {
                    double similarity = similaritySearch.getLastSimilarities().get(i);
                    String entry = String.format("%d. %s  (%.1f%%)", 
                                                  i + 1, matches.get(i), similarity * 100);
                    matchListView.getItems().add(entry);
                }
                matchStatusLabel.setText("Top " + matches.size() + " matches found");
                statusLabel.setText("Identification complete");
            }
            
            progressBar.setProgress(1.0);
            
        } catch (Exception e) {
            showAlert("Identification Error", e.getMessage());
            statusLabel.setText("Error during identification");
            progressBar.setProgress(0);
            e.printStackTrace();
        } finally {
            identifyButton.setDisable(false);
        }
    }
    
    @FXML
    private void handleLoadPathImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image for Pathfinding");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );
        
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                currentPathImage = imageLoader.loadImage(selectedFile);
                pathImageView.setImage(currentPathImage);
                
                // Reset state
                isStartPointSelected = false;
                startPointLabel.setText("Start: (click image to set)");
                endPointLabel.setText("End: (click image to set)");
                findPathButton.setDisable(true);
                pathListView.getItems().clear();
                
                statusLabel.setText("Pathfinding image loaded: " + selectedFile.getName());
                
                showAlert("Pathfinding Ready", 
                          "Click on the image to set a start point, then click again to set an end point.");
            } catch (Exception e) {
                showAlert("Error", "Failed to load image: " + e.getMessage());
                statusLabel.setText("Error loading pathfinding image");
            }
        }
    }
    
    @FXML
    private void handleFindPath() {
        if (currentPathImage == null) {
            showAlert("No Image", "Please load an image for pathfinding.");
            return;
        }
        
        statusLabel.setText("Computing shortest path...");
        progressBar.setProgress(0.5);
        findPathButton.setDisable(true);
        
        // TODO: Implement actual pathfinding
        // This is a placeholder – we'll implement this properly when we build the pathfinding module
        pathListView.getItems().clear();
        pathListView.getItems().add("Start: (" + String.format("%.0f", clickX) + ", " + 
                                    String.format("%.0f", clickY) + ")");
        pathListView.getItems().add("End: (" + String.format("%.0f", clickX) + ", " + 
                                    String.format("%.0f", clickY) + ")");
        pathListView.getItems().add("Path length: 42.0 units");
        pathListView.getItems().add("Nodes visited: 156");
        
        statusLabel.setText("Pathfinding complete");
        progressBar.setProgress(1.0);
        findPathButton.setDisable(false);
        
        showAlert("Pathfinding Demo", "Full pathfinding implementation coming soon!");
    }
    
    @FXML
    private void handleClearQuery() {
        queryImageView.setImage(null);
        queryPathField.clear();
        matchListView.getItems().clear();
        matchStatusLabel.setText("");
        currentQueryFile = null;
        identifyButton.setDisable(true);
        statusLabel.setText("Query cleared");
    }
    
    @FXML
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
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About WildTrack");
        alert.setHeaderText("WildTrack – Non‑Invasive Wildlife Monitoring");
        alert.setContentText(
            "Version: 1.0\n\n" +
            "A desktop application using graph-based data structures for:\n" +
            "• Individual animal identification\n" +
            "• Drone path planning\n\n" +
            "Built for CS 3A Mini Project\n" +
            "All data structures implemented from scratch."
        );
        alert.showAndWait();
    }
    
    @FXML
    private void handleExit() {
        javafx.application.Platform.exit();
    }
    
    // ===== Helper Methods =====
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
	
}
