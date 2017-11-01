#include <stdio.h>
#include <fstream>
#include <iostream>

using namespace std;

const int mLen = 26;
int width = 0;
int types = 0;
float mTime = 0;
int x, y;
char playerMat[mLen][mLen];
char backupMat1[mLen][mLen];
bool visitedMat1[mLen][mLen];
char oppoMat[mLen][mLen];
char backupMat2[mLen][mLen];
bool visitedMat2[mLen][mLen];
char curType;
int counter = 0;
int path[mLen * mLen][2];

void initVisited();
void readFile();
void writeFile();
void setVisited(char mat[mLen][mLen], bool visited[mLen][mLen]);
bool checkMat();
void copyMat(char mat1[mLen][mLen], char mat2[mLen][mLen]);
void calculate(char mat[mLen][mLen], int x1, int y1);
void fallDown(char mat[mLen][mLen]);
void MinMax();

int main(int argc, char* argv[]) {
	initVisited();
	readFile();
	setVisited(playerMat, visitedMat1);
	if (checkMat() == false) {
		return 0;
	}
	MinMax();
	writeFile();
	return 0;
}

void initVisited() {
	for (int i = 0; i < width; i++)
		for (int j = 0; j < width; j++) {
			visitedMat1[i][j] = false;
			visitedMat2[i][j] = false;
		}
}

void readFile() {
	ifstream file;
	file.open("input.txt", ios::in);
	file >> width;
	file >> types;
	file >> mTime;
	file.getline(playerMat[0], mLen);
	for (int i = 0; i < width; i++) {
		file.getline(playerMat[i], mLen);
	}
	file.close();
}

void writeFile() {
	ofstream ofile;
	ofile.open("output.txt", ios::trunc | ios::out);
	char ch = y + 'A';
	x = x + 1;
	ofile << ch << x << endl;
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < width - 1; j++) {
			ofile << playerMat[i][j];
		}
		ofile << playerMat[i][width - 1] << endl;
	}
	ofile.close();
}

void setVisited(char mat[mLen][mLen], bool visited[mLen][mLen]) {
	for (int i = 0; i < width; i++)
		for (int j = 0; j < width; j++) {
			if (mat[i][j] == '*')
				visited[i][j] = true;
		}
}

bool checkMat() {
	for (int i = 0; i < width; i++)
		for (int j = 0; j < width; j++) {
			if (!visitedMat1[i][j]) {
				return true;
			}
		}
	return false;
}

void copyMat(char mat1[mLen][mLen], char mat2[mLen][mLen]) {
	for (int i = 0; i < width; i++)
		for (int j = 0; j < width; j++) {
			mat2[i][j] = mat1[i][j];
		}
}

void calculate(char mat[mLen][mLen], int x1, int y1) {
	if (mat[x1][y1] == curType) {
		for (int i = 0; i < counter; i++) {
			if (path[i][0] == x1 && path[i][1] == y1) {
				return;
			}
		}
		mat[x1][y1] = '*';
		path[counter][0] = x1;
		path[counter][1] = y1;
		counter++;
	} else {
		return;
	}
	int x[4];
	int y[4];
	x[0] = x1;
	y[0] = y1 - 1;
	x[1] = x1 + 1;
	y[1] = y1;
	x[2] = x1;
	y[2] = y1 + 1;
	x[3] = x1 - 1;
	y[3] = y1;
	for (int i = 0; i < 4; i++) {
		if (x[i] >= 0 && x[i] < width && y[i] >= 0 && y[i] < width) {
			calculate(mat, x[i], y[i]);
		}
	}
}

void fallDown(char mat[mLen][mLen]) {
	for (int j = 0; j < width; j++) {
		for (int i = width - 1; i >= 0; i--) {
			if (mat[i][j] == '*') {
				for (int k = i; k > 0; k--) {
					mat[k][j] = mat[k - 1][j];
				}
				mat[0][j] = '*';
				bool find = false;
				for (int h = 0; h <= i; h++) {
					if (mat[h][j] != '*') {
						find = true;
						break;
					}
				}
				if (!find) {
					break;
				}
				if (mat[i][j] == '*') {
					i++;
				}
			}
		}
	}
}

void MinMax() {
	int max = -32768;
	int player = 0;
	int opponent = 0;
	for (int i = 0; i < width; i++) {
		for (int j = 0; j < width; j++) {
			if (!visitedMat1[i][j]) {
				copyMat(playerMat, backupMat1);
				curType = playerMat[i][j];
				counter = 0;
				calculate(playerMat, i, j);
				player = counter;
				copyMat(playerMat, oppoMat);
				fallDown(oppoMat);
				setVisited(oppoMat, visitedMat2);
				opponent = 0;
				for (int k = 0; k < width; k++) {
					for (int h = 0; h < width; h++) {
						if (!visitedMat2[k][h]) {
							copyMat(oppoMat, backupMat2);
							curType = oppoMat[k][h];
							counter = 0;
							calculate(oppoMat, k, h);
							if (counter > opponent) {
								opponent = counter;
							}
							setVisited(backupMat2, visitedMat2);
							copyMat(backupMat2, oppoMat);
						}
					}
				}
				player = player * player;
				opponent = opponent * opponent;
				if ((player - opponent) > max) {
					max = player - opponent;
					x = i;
					y = j;
				}
				setVisited(playerMat, visitedMat1);
				copyMat(backupMat1, playerMat);
			}
		}
	}
	curType = playerMat[x][y];
	counter = 0;
	calculate(playerMat, x, y);
	fallDown(playerMat);
}
