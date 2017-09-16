package solutions;

public class DFS {
	public static int[][] run(int edge_length, int number_of_lizards, int[][] nursery) {
		int[][] result = new int[edge_length][edge_length];
		int number_of_trees = 0;
		for (int i = 0; i < edge_length; i++) {
			for (int j = 0; j < edge_length; j++) {
				result[i][j] = nursery[i][j];
				if (nursery[i][j] == 2) {
					number_of_trees++;
				}
			}
		}

		if (number_of_lizards > edge_length * edge_length - number_of_trees) { // no enough space for lizards
			return null;
		}
		
		boolean[] isOK = new boolean[1];
		helper(edge_length, number_of_lizards, nursery, 0, 0, result, isOK);
		return isOK[0] ? result : null;
	}

	// top-down DFS
	private static void helper(int edge_length, int number_of_lizards, int[][] nursery, int i_start, int j_start, int[][] result, boolean[] isOK) {
		if (number_of_lizards == 0) {
			isOK[0] = true;
			return;
		}

		for (int i = i_start; i < edge_length; i++) {
			int j = j_start;
			if (i != i_start) {
				j = 0;
			}
			for (; j < edge_length; j++) {
				// if cur position is a tree
				if (nursery[i][j] == 2) {
					continue;
				}
				// put a lizard here, then go to next coordinate
				result[i][j] = 1;
				if (isValidPosition(i, j, result)) {
					if (j != edge_length - 1) {
						helper(edge_length, (number_of_lizards - 1), nursery, i, (j + 1), result, isOK);
					} else {
						helper(edge_length, (number_of_lizards - 1), nursery, (i + 1), 0, result, isOK);
					}
				}
				// if it has found a solution, stop searching, 
				// otherwise, current position should not put lizard, remove it and continue searching
				if (isOK[0]) {
					return;
				} else {
					result[i][j] = 0;
				}
			}
		}
	}
	
	// note: this method does not checking (i, j) itself
	// it only checks if (i, j) conflicts with another girds
	private static boolean isValidPosition(int i, int j, int[][] result) {
		int x;
		int y;
		/*
			O------------Y
			|
			|
			|
			|
			X
		*/
		// check up
		x = i;
		y = j;
		while (x > 0) {
			if (result[x - 1][y] == 2) {
				break;
			}
			if (result[x - 1][y] == 1) {
				return false;
			}
			if (result[x - 1][y] == 0) {
				x--;
			}
		}		
		// check down
		x = i;
		y = j;
		while (x < result.length - 1) {
			if (result[x + 1][y] == 2) {
				break;
			}
			if (result[x + 1][y] == 1) {
				return false;
			}
			if (result[x + 1][y] == 0) {
				x++;
			}
		}	
		
		// check left
		x = i;
		y = j;
		while (y > 0) {
			if (result[x][y - 1] == 2) {
				break;
			}
			if (result[x][y - 1] == 1) {
				return false;
			}
			if (result[x][y - 1] == 0) {
				y--;
			}
		}			
		// check right
		x = i;
		y = j;
		while (y < result.length - 1) {
			if (result[x][y + 1] == 2) {
				break;
			}
			if (result[x][y + 1] == 1) {
				return false;
			}
			if (result[x][y + 1] == 0) {
				y++;
			}
		}
		// check left-top direction
		x = i;
		y = j;
		while (x > 0 && y > 0) {
			if (result[x - 1][y - 1] == 2) {
				break;
			}
			if (result[x - 1][y - 1] == 1) {
				return false;
			}
			if (result[x - 1][y - 1] == 0) {
				x--;
				y--;
			}
		}
		// check right-top direction
		x = i;
		y = j;
		while (x > 0 && y < result.length - 1) {
			if (result[x - 1][y + 1] == 2) {
				break;
			}
			if (result[x - 1][y + 1] == 1) {
				return false;
			}
			if (result[x - 1][y + 1] == 0) {
				x--;
				y++;
			}
		}		
		// check left-bottom direction
		x = i;
		y = j;
		while (x < result.length - 1 && y > 0) {
			if (result[x + 1][y - 1] == 2) {
				break;
			}
			if (result[x + 1][y - 1] == 1) {
				return false;
			}
			if (result[x + 1][y - 1] == 0) {
				x++;
				y--;
			}
		}			
		// check right-bottom direction
		x = i;
		y = j;
		while (x < result.length - 1 && y < result.length - 1) {
			if (result[x + 1][y + 1] == 2) {
				break;
			}
			if (result[x + 1][y + 1] == 1) {
				return false;
			}
			if (result[x + 1][y + 1] == 0) {
				x++;
				y++;
			}
		}	
		return true;
	}
}
