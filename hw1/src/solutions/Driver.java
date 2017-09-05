package solutions;

public class Driver {

	public static void main(String[] args) {
		Input input = ReadInput.getInput();
		input.print(); // debug
		
		int[][] result = null;
		if (input.getMethod().equals("DFS")) {
			result = DFS.runDFS(input.getEdge_length(), input.getNumber_of_lizards(), input.getNursery());
		}
		if (result != null) {
			System.out.println("---------------------------");
			System.out.println("OK");
			for (int i = 0; i < input.getEdge_length(); i++) {
				for (int j = 0; j < input.getEdge_length(); j++) {
					System.out.print(result[i][j]);
				}
				System.out.println();
			}
		} else {
			System.out.println("FAIL");
		}
	}

}
