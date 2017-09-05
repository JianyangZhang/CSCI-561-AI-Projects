package solutions;

public class Input {
	private String method;
	private int edge_length;
	private int number_of_lizards;
	private int[][] nursery;
	public Input(String method, int edge_length, int number_of_lizards, int[][] nursery) {
		this.method = method;
		this.edge_length = edge_length;
		this.number_of_lizards = number_of_lizards;
		this.nursery = nursery;
	}
	public String getMethod() {
		return method;
	}
	public int getEdge_length() {
		return edge_length;
	}
	public int getNumber_of_lizards() {
		return number_of_lizards;
	}
	public int[][] getNursery() {
		return nursery;
	}
	public void print() {
		System.out.println(method);
		System.out.println(edge_length);
		System.out.println(number_of_lizards);
		for (int i = 0; i < nursery.length; i++) {
			for (int j = 0; j < nursery[0].length; j++) {
				System.out.print(nursery[i][j]);
			}
			System.out.println();
		}
	}
}
