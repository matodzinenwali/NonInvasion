package gui;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageViewer extends ImageView {
    
    public void drawPath(java.util.List<int[]> pathPoints, Color color) {
        if (getImage() == null) return;
        
        // Load image as writable for drawing
        int width = (int) getImage().getWidth();
        int height = (int) getImage().getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter writer = writableImage.getPixelWriter();
        
        // Copy original image
        // (Simplified – in real implementation, you'd use PixelReader to copy)
        
        // Draw path points
        for (int[] point : pathPoints) {
            int x = point[0];
            int y = point[1];
            if (x >= 0 && x < width && y >= 0 && y < height) {
                writer.setColor(x, y, color);
            }
        }
        
        setImage(writableImage);
    }
}
