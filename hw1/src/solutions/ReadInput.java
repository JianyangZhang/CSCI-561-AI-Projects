package solutions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ReadInput {
	public static final String INPUT_FILE_PATH = "./input.txt";
	public static Input getInput(){
		Input input = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE_PATH));
			String method = br.readLine().trim();
			int edge_length = Integer.parseInt(br.readLine().trim());
			int number_of_lizards = Integer.parseInt(br.readLine().trim());
			int[][] nursery = new int[edge_length][edge_length];
			for (int i = 0; i < edge_length; i++) {
				String curLine = br.readLine().trim();
				for (int j = 0; j < edge_length; j++) {
					nursery[i][j] = Character.getNumericValue(curLine.charAt(j));
				}
			}
			input = new Input(method, edge_length, number_of_lizards, nursery);
			br.close();
		} catch (Exception e) {
			System.out.println("failed to read input.txt");
			e.printStackTrace();
		}
		return input;
	}
}
