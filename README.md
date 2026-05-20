Project structure  

├── main/                     - Main class & JavaFX launcher  
│   └── Main.java          (extends Application)  
│  
├── datastructures/           - custom implementations (no java.util.*)  
│   ├── Graph.java            (ADT: nodes + edges, adjacency list using your own List)  
│   ├── Heap.java             (min heap for priority queue)  
│   ├── Queue.java            (simple queue – can use LinkedList you write)  
│   ├── Dictionary.java       (map interface – hash table or trie)  
│   ├── List.java             (singly- for adjacency)  
│   └── Tree.java             (general tree or binary search tree, e.g., KD‑Tree)  
│  
├── image/                    - Image loading & processing (no external APIs)  
│   ├── ImageLoader.java      (reads BufferedImage from local disk)  
│   ├── FeatureExtractor.java (converts image to a feature vector or graph)  
│   └── GraphBuilder.java     (builds a Graph from an image: pixels/regions as nodes)  
│  
├── similarity/               - Matching individuals using your data structures  
│   ├── FeatureDictionary.java (uses your Dictionary to store feature vectors keyed by animal ID)  
│   ├── SimilaritySearch.java   (uses Heap to find top‑K nearest neighbours)  
│   └── DistanceMetric.java     (Euclidean)  
│  
├── pathfinding/              - Pathfinding on an image (for drone planning)  
│   ├── ImageGraphAdapter.java (interprets an image as a grid graph for pathfinding)  
│   └── ShortestPathFinder.java (uses Queue (BFS) or Heap (Dijkstra))  
│  
├── gui/                      - JavaFX views  
│   ├── MainController.java
│   ├── ImageViewer.java
│   └── ResultDisplay.java
│
└── domain/                   - Business objects
    ├── Animal.java           (id, species, feature vector)
    └── ImageRegion.java      (node representation: coordinates, colour, texture)
