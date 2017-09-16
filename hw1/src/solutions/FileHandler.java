package solutions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class FileHandler {
	public static final String INPUT_FILE_PATH = "./input.txt";
	public static final String OUTPUT_FILE_PATH = "./output.txt";
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
	public static void writeOutput(int[][] result){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
			if (result == null) {
				bw.write("FAIL");
			} else {
				bw.write("OK");
				bw.newLine();
				for (int i = 0; i < result.length; i++) {
					for (int j = 0; j < result[0].length; j++) {
						bw.write(Integer.toString(result[i][j]));
					}
					bw.newLine();
				}
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("failed to write output.txt");
			e.printStackTrace();
		}
	}
}
