

import java.util.LinkedList;
import java.util.Queue;

class State {
	int number_of_lizards;
	int[][] nursery;
	int last_i;
	int last_j;
	public State(int number_of_lizards, int[][] nursery, int last_i, int last_j) {
		this.number_of_lizards = number_of_lizards; 
		this.nursery = nursery;
		this.last_i = last_i;
		this.last_j = last_j;
	}
}

public class BFS {
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
		
		return helper(edge_length, number_of_lizards, nursery);
	}
	
	private static int[][] helper(int edge_length, int number_of_lizards, int[][] nursery) {
		long startTime = System.currentTimeMillis();
		long timeElapsed;
		Queue<State> queue = new LinkedList<>();
		queue.offer(new State(number_of_lizards, nursery, 0, -1));

		while (!queue.isEmpty()) {
			State state = queue.poll();
			if (state.number_of_lizards == 0) {
				return state.nursery;
			}
			timeElapsed = System.currentTimeMillis() - startTime;
			if (timeElapsed >= 297000) {
				return null;
			}
			int start_i;
			int start_j;
			if (state.last_j + 1 > edge_length - 1) {
				if (state.last_i + 1 > edge_length - 1) {
					continue;
				}
				start_i = (state.last_i + 1);
				start_j = 0;
			} else {
				start_i = state.last_i;
				start_j = (state.last_j + 1);
			}
			
			for (int i1 = start_i; i1 < edge_length; i1++) {
				int j = start_j;
				if (i1 != start_i) {
					j = 0;
				}
				for (; j < edge_length; j++) {
					if (nursery[i1][j] == 2) {
						continue;
					}
					int[][] newNursery = new int[edge_length][edge_length];
					copyArray(state.nursery, newNursery);
					newNursery[i1][j] = 1;
					if (isValidPosition(i1, j, newNursery)) {
						queue.offer(new State((state.number_of_lizards - 1), newNursery, i1, j));
					}
				}
			}
		}
		return null;
	}
	
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
	
	private static void copyArray(int[][] origin, int[][] newArray) {
		for (int i = 0; i < origin.length; i++) {
			for (int j = 0; j < origin[0].length; j++) {
				newArray[i][j] = origin[i][j]; 
			}
		}
	}
}
