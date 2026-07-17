package datastructures;

//helper class for adjacency list
public class Edge<T> {
	private T destination;
	private double weight;
	
	public Edge(T destination, double weight) {
		this.destination = destination;
		this.weight = weight;
	}
	
	public T getDestination() {
		return destination;
	}

	public double getWeight() {
		return weight;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		Edge<?> edge = (Edge<?>) obj;
		return Double.compare(edge.weight, weight) == 0 &&
				destination.equals(edge.destination);
		
	}
	
	@Override
	public int hashCode() {
		return destination.hashCode() * 31 + Double.hashCode(weight);
	}
}
