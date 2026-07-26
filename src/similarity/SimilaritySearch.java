package similarity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimilaritySearch {
    @SuppressWarnings("unused")
	private final AnimalDatabase database;
    private List<Double> lastSimilarities;
    
    public SimilaritySearch(AnimalDatabase database) {
        this.database = database;
        this.lastSimilarities = new ArrayList<>();
    }
    
    public List<String> findMatches(File queryFile, int topK) {
        List<String> matches = new ArrayList<>();
        lastSimilarities.clear();
        
        // Placeholder
        for (int i = 0; i < topK; i++) {
            matches.add("Animal_" + (i + 1));
            lastSimilarities.add(0.85 - (i * 0.1));
        }
        
        return matches;
    }
    
    public List<Double> getLastSimilarities() {
        return lastSimilarities;
    }
}
