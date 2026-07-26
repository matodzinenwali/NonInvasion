package similarity;

import java.io.File;

import datastructures.Tree;
import image.FeatureExctractor;

public class AnimalDatabase {
    private final Tree<String> tree;
    private int size;
    
    public AnimalDatabase(FeatureExctractor extractor) {
        this.tree = new Tree<>(10);
        this.size = 0;
    }
    
    public void addAnimal(String id, File imageFile) {
        // Will be implemented when we build the full feature extraction
        double[] features = new double[10];
        tree.insert(features, id);
        size++;
    }
    
    public int getSize() {
        return size;
    }
}
