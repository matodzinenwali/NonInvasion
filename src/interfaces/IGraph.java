package interfaces;

public interface IGraph<T> {
    void addNode(T node);
    void addEdge(T from, T to, double weight);
    IList<T> getNeighbours(T node);
    double getWeight(T from, T to);
    IList<T> getAllNodes();
    int nodeCount();
    int edgeCount();
    boolean containsNode(T node);
    boolean containsEdge(T from, T to);
    void removeNode(T node);
    void removeEdge(T from, T to);
    IList<T> shortestPath(T source, T dstination);
}
