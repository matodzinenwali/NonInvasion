package datastructures;

import java.util.Iterator;

import interfaces.IDictionary;
import interfaces.IGraph;
import interfaces.IHeap;
import interfaces.IIterator;
import interfaces.IList;

public class Graph<T> implements IGraph<T> {
	
	//Dictionary mapping node - list of edges
	private final IDictionary<T, IList<Edge<T>>> adjacencyList;
	private int edgeCount;
	
	public Graph() {
		this.adjacencyList = new Dictionary<>();
		this.edgeCount = 0;
	}

	@Override
	public void addNode(T node) {
		if (node == null) {
			throw new IllegalArgumentException("Node cannot be null");
		}
		if(!adjacencyList.containsKey(node)) {
			adjacencyList.put(node,  new LinkedList<>());
		}
		
	}

	@Override
	public void addEdge(T from, T to, double weight) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("Nodes cannot be null");
		}
		if (weight < 0) {
			throw new IllegalArgumentException("Weight cannot be negative");
		}
		//Ensure both nodes exist
		addNode(from);
		addNode(to);
		
		IList<Edge<T>> edgesFrom = adjacencyList.get(from);
		//check if edge already exist
		IIterator<Edge<T>> it = edgesFrom.iterator();
		while(it.hasNext()) {
			Edge<T> e = it.next();
			if (e.getDestination().equals(to)) {
				/*
				 * Update weight
				 * we nned to modify the edge, but edge is immutable, so remove and re-add
				 */
				it.remove();
				edgesFrom.add(new Edge<>(to, weight));
				return;
				
			}
		}
		//New Edge
		edgesFrom.add(new Edge<>(to, weight));
		edgeCount++;
		
	}

    @Override
    public IList<T> getNeighbours(T node) {
        if (!adjacencyList.containsKey(node)) {
            return new LinkedList<>();
        }
        IList<Edge<T>> edges = adjacencyList.get(node);
        IList<T> neighbours = new LinkedList<>();
        IIterator<Edge<T>> it = edges.iterator();
        while (it.hasNext()) {
            neighbours.add(it.next().getDestination());
        }
        return neighbours;
    }
    
    @Override
    public double getWeight(T from, T to) {
        if (!adjacencyList.containsKey(from)) {
            return Double.POSITIVE_INFINITY;
        }
        IList<Edge<T>> edges = adjacencyList.get(from);
        IIterator<Edge<T>> it = edges.iterator();
        while (it.hasNext()) {
            Edge<T> e = it.next();
            if (e.getDestination().equals(to)) {
                return e.getWeight();
            }
        }
        return Double.POSITIVE_INFINITY;
    }
    
    @Override
    public IList<T> getAllNodes() {
        return adjacencyList.keys();
    }
    
    @Override
    public int nodeCount() {
        return adjacencyList.size();
    }
    
    @Override
    public int edgeCount() {
        return edgeCount;
    }
    
    @Override
    public boolean containsNode(T node) {
        return adjacencyList.containsKey(node);
    }
    
    @Override
    public boolean containsEdge(T from, T to) {
        if (!adjacencyList.containsKey(from)) {
            return false;
        }
        IList<Edge<T>> edges = adjacencyList.get(from);
        IIterator<Edge<T>> it = edges.iterator();
        while (it.hasNext()) {
            if (it.next().getDestination().equals(to)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void removeNode(T node) {
        if (!adjacencyList.containsKey(node)) {
            return;
        }
        // Remove all edges from this node
        IList<Edge<T>> edgesToRemove = adjacencyList.get(node);
        edgeCount -= edgesToRemove.size();
        adjacencyList.remove(node);
        
        // Remove all edges pointing to this node from other nodes
        IList<T> allNodes = adjacencyList.keys();
        IIterator<T> nodeIt = allNodes.iterator();
        while (nodeIt.hasNext()) {
            T other = nodeIt.next();
            IList<Edge<T>> otherEdges = adjacencyList.get(other);
            IIterator<Edge<T>> edgeIt = otherEdges.iterator();
            while (edgeIt.hasNext()) {
                if (edgeIt.next().getDestination().equals(node)) {
                    edgeIt.remove();
                    edgeCount--;
                }
            }
        }
    }
    
    @Override
    public void removeEdge(T from, T to) {
        if (!adjacencyList.containsKey(from)) {
            return;
        }
        IList<Edge<T>> edges = adjacencyList.get(from);
        IIterator<Edge<T>> it = edges.iterator();
        while (it.hasNext()) {
            if (it.next().getDestination().equals(to)) {
                it.remove();
                edgeCount--;
                return;
            }
        }
    }
    
    @Override
    public IList<T> shortestPath(T source, T destination) {
        if (!containsNode(source) || !containsNode(destination)) {
            return new LinkedList<>();
        }
        if (source.equals(destination)) {
            IList<T> path = new LinkedList<>();
            path.add(source);
            return path;
        }
        
        // Dijkstra's algorithm using our MinHeap
        IDictionary<T, Double> distances = new Dictionary<>();
        IDictionary<T, T> previous = new Dictionary<>();
        IHeap<PathNode<T>> heap = new MinHeap<>();
        
        // Initialize distances
        IList<T> allNodes = getAllNodes();
        IIterator<T> it = allNodes.iterator();
        while (it.hasNext()) {
            T node = it.next();
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(source, 0.0);
        heap.insert(new PathNode<>(source, 0.0));
        
        while (!heap.isEmpty()) {
            PathNode<T> current = heap.extractMin();
            T currentVertex = current.node;
            double currentDist = current.distance;
            
            // If we reached the destination, reconstruct path
            if (currentVertex.equals(destination)) {
                return reconstructPath(previous, destination);
            }
            
            // Skip if we already found a better path
            if (currentDist > distances.get(currentVertex)) {
                continue;
            }
            
            IList<Edge<T>> edges = adjacencyList.get(currentVertex);
            if (edges == null) continue;
            IIterator<Edge<T>> edgeIt = edges.iterator();
            while (edgeIt.hasNext()) {
                Edge<T> edge = edgeIt.next();
                T neighbor = edge.getDestination();
                double newDist = currentDist + edge.getWeight();
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    previous.put(neighbor, currentVertex);
                    heap.insert(new PathNode<>(neighbor, newDist));
                }
            }
        }
        // No path found
        return new LinkedList<>();
    }
    
    private IList<T> reconstructPath(IDictionary<T, T> previous, T destination) {
        IList<T> path = new LinkedList<>();
        T current = destination;
        while (current != null) {
            // add to front of list (we'll add and then reverse)
            path.add(current);
            current = previous.get(current);
        }
        // Reverse the list (since we have destination to source)
        IList<T> reversedPath = new LinkedList<>();
        for (int i = path.size() - 1; i >= 0; i--) {
            reversedPath.add(path.get(i));
        }
        return reversedPath;
    }
    
    // Helper class for heap entries
    private static class PathNode<T> implements Comparable<PathNode<T>> {
        T node;
        double distance;
        
        @SuppressWarnings("unused")
		PathNode(T node, double distance) {
            this.node = node;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(PathNode<T> other) {
            return Double.compare(this.distance, other.distance);
        }
    }
	
	

}
