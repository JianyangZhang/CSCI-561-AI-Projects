package solutions;

import java.util.Random;

public class SA {
	private static Random rand = new Random();
	public static int[][] run(int edge_length, int number_of_lizards, int[][] nursery) {
		int number_of_trees = 0;
		for (int i = 0; i < edge_length; i++) {
			for (int j = 0; j < edge_length; j++) {
				if (nursery[i][j] == 2) {
					number_of_trees++;
				}
			}
		}

		if (number_of_lizards > edge_length * edge_length - number_of_trees) { // no enough space for lizards
			return null;
		}
		
		return helper(edge_length, number_of_lizards, nursery, Integer.MAX_VALUE);
	}
	
	private static int[][] helper(int edge_length, int number_of_lizards, int[][] nursery, int temperature) {
		// initialize a result
		int[][] result = new int[edge_length][edge_length];
		copyArray(nursery, result);
		randomize(edge_length, number_of_lizards, result);
		int currentCost = getTotalCost(result);
		
		// find a better result
		for (int i = temperature; i > 0; i--) {
			if (currentCost == 0) {
				return result;
			} else {
				int[][] nextResult = new int[edge_length][edge_length];
				copyArray(result, nextResult);
				getNextState(edge_length, nextResult);
				int nextCost = getTotalCost(nextResult);
				int diff = currentCost - nextCost;
                double threshold = Math.exp(diff / i);
                double probability = Math.random();
                if (diff > 0) {
                	result = nextResult;
                	currentCost = nextCost;
                } else {
                	if (probability <= threshold) {
                		result = nextResult;
                		currentCost = nextCost;
                	}
                }
			}
		}
		return null;
	}
	
	private static void randomize(int edge_length, int number_of_lizards, int[][] result) {
		int row, col;
		while (number_of_lizards > 0) {
			row = rand.nextInt(edge_length);
			col = rand.nextInt(edge_length);
			if (result[row][col] == 0) {
				result[row][col] = 1;
				number_of_lizards--;
			}
		}
	}
	
	private static void getNextState(int edge_length, int[][] result) {
		int row = rand.nextInt(edge_length);
		int col = rand.nextInt(edge_length);
		// pick a queen
		while (result[row][col] != 1) {
			row = rand.nextInt(edge_length);
			col = rand.nextInt(edge_length);
		}
		// remove this queen
		result[row][col] = 0;
		
		// find another empty grid
		row = rand.nextInt(edge_length);
		col = rand.nextInt(edge_length);
		while(result[row][col] != 0) {
			row = rand.nextInt(edge_length);
			col = rand.nextInt(edge_length);
		}
		result[row][col] = 1;
	}
	
	public static int getTotalCost(int[][] result) {
        int cost = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
        		if (result[i][j] == 1) {
        			cost += getCost(result, i, j);
        		}
            }
        }
        return cost / 2;
    }

	private static int getCost(int[][] result, int i, int j) {
		int cost = 0;
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
				cost++;
			}
			x--;
		}		
		// check down
		x = i;
		y = j;
		while (x < result.length - 1) {
			if (result[x + 1][y] == 2) {
				break;
			}
			if (result[x + 1][y] == 1) {
				cost++;
			}
			x++;
		}	
		
		// check left
		x = i;
		y = j;
		while (y > 0) {
			if (result[x][y - 1] == 2) {
				break;
			}
			if (result[x][y - 1] == 1) {
				cost++;
			}
			y--;
		}			
		// check right
		x = i;
		y = j;
		while (y < result.length - 1) {
			if (result[x][y + 1] == 2) {
				break;
			}
			if (result[x][y + 1] == 1) {
				cost++;
			}
			y++;
		}
		// check left-top direction
		x = i;
		y = j;
		while (x > 0 && y > 0) {
			if (result[x - 1][y - 1] == 2) {
				break;
			}
			if (result[x - 1][y - 1] == 1) {
				cost++;
			}
			x--;
			y--;
		}
		// check right-top direction
		x = i;
		y = j;
		while (x > 0 && y < result.length - 1) {
			if (result[x - 1][y + 1] == 2) {
				break;
			}
			if (result[x - 1][y + 1] == 1) {
				cost++;
			}
			x--;
			y++;
		}		
		// check left-bottom direction
		x = i;
		y = j;
		while (x < result.length - 1 && y > 0) {
			if (result[x + 1][y - 1] == 2) {
				break;
			}
			if (result[x + 1][y - 1] == 1) {
				cost++;
			}
			x++;
			y--;
		}			
		// check right-bottom direction
		x = i;
		y = j;
		while (x < result.length - 1 && y < result.length - 1) {
			if (result[x + 1][y + 1] == 2) {
				break;
			}
			if (result[x + 1][y + 1] == 1) {
				cost++;
			}
			x++;
			y++;
		}	
		return cost;
	}
	private static void copyArray(int[][] origin, int[][] newArray) {
		for (int i = 0; i < origin.length; i++) {
			for (int j = 0; j < origin[0].length; j++) {
				newArray[i][j] = origin[i][j]; 
			}
		}
	}
}
