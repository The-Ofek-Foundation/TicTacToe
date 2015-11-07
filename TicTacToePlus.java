/**
 * An implementation of the game Tic Tac Toe
 * in Java, with an artificial intelligence using
 * depth first search, an another using Monte
 * Carlo Tree Search.
 * 
 * @author Ofek Gila
 * @since September 3rd, 2015
 */

import java.util.Scanner;
import java.util.ArrayList;

public class TicTacToePlus {

	/**
	 * The expansion constant is used for analyzing child
	 * potential in Monte Carlo (look up Monte Carlo Tree
	 * Search on Wikipedia for more information)
	 */
	public static final double expansionConstant = 2;

	// Increase number of trials (to 100000 or 1000000) to
	// increase ai strength (shouldn't be necessary for tic tac toe)
	public static final int monteCarloTrials = 100000;

	public char[][] board;
	public boolean xTurn;
	/**
	 * aiTurn is 0 for no ai, 1 for X and 0 for O
	 */
	public int aiTurn;

	/**
	 * Anti Tic Tac Toe is a game where the goal is
	 * for your opponent to win a game of Tic Tac Toe
	 */
	public static boolean antiTicTacToe = false;

	public TicTacToeMCTSNode root;
	public boolean monteCarloMode = true;

	public TicTacToePlus(int aiTurn) {
		this.aiTurn = aiTurn;
		xTurn = true;
		board = new char[3][3];
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				board[i][a] = ' ';
		root = nextRoot(-1, -1);
	}

	/**
	 * This functions print out a passed board
	 * @param board The passed board
	 */
	public static void printBoard(char[][] board) {
		System.out.println();
		for (int i = 0; i < board.length; i++) {
			System.out.print("   " + board[i][0]);
			for (int a = 1; a < board[i].length; a++)
				System.out.print("|" + board[i][a]);
			System.out.println();
			System.out.print("   ");
			if (i < board.length - 1)
				for (int b = 0; b < board.length * 2 - 1; b++)
					System.out.print("-");
			System.out.println(); 
		}
		System.out.println();
	}

	/**
	 * @param pumpkins ai turn as parameter
	 */
	public static void main(String... pumpkins) {
		// Pass 0 for no ai, 1 for ai as X and -1 for ai as O
		TicTacToePlus TTT = new TicTacToePlus(pumpkins.length > 0 ? Integer.parseInt(pumpkins[0]):0);
		TTT.run();
	}

	/**
	 * This function runs the game
	 */
	public void run() {
		System.out.println("\nWelcome to Tic Tac Toe!");

		printBoard(board);
		while (!gameOver(board)) {
			playMove();
			printBoard(board);
		}
		printResult();
	}

	/**
	 * This function checks if the game is win
	 * on the board, returing either a -1 (circles win),
	 * 1 (Xs win), or 0 (neither won)
	 * 
	 * @return int Result of game
	 */
	public static int gameResult(char[][] board) {

		char color;
		int consecutive;

		// check vertical
		for (int i = 0; i < board.length; i++) {
			consecutive = 0;
			color = '?';
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else consecutive = 0;
			if (consecutive == 3)
				return color == 'O' == antiTicTacToe ? 1:-1;
		}

		// check horizontal
		for (int a = 0; a < board[0].length; a++) {
			consecutive = 0;
			color = '?';
			for (int i = 0; i < board.length; i++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else consecutive = 0;
			if (consecutive == 3)
				return color == 'O' == antiTicTacToe ? 1:-1;
		}

		// check top-left to bottom-right diagonal
		consecutive = 0;
		color = '?';
		for (int i = 0, a = 0; i < board.length; i++, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else consecutive = 0;
		if (consecutive == 3)
			return color == 'O' == antiTicTacToe ? 1:-1;

		// check top-right to bottom-left diagonal
		consecutive = 0;
		color = '?';
		for (int i = board.length - 1, a = 0; i >= 0; i--, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else consecutive = 0;
		if (consecutive == 3)
			return color == 'O' == antiTicTacToe ? 1:-1;

		return 0;
	}

	/**
	 * This function returns true if the game is over,
	 * false otherwise
	 * 
	 * @param  board   The current state of the board
	 * @return boolean Whether or not game is over
	 */
	public static boolean gameOver(char[][] board) {
		if (gameResult(board) != 0)
			return true;

		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ')
					return false;

		return true;
	}

	/**
	 * Plays a move
	 * 
	 * @param x X coord
	 * @param y Y coord
	 */
	public void playMove(int x, int y) {
		board[x][y] = xTurn ? 'X':'O';
		xTurn = !xTurn;
		// In Monte Carlo Tree Search, you can reuse the subtree of
		// the previous root to be more efficient.
		root = nextRoot(x, y);
	}

	/**
	 * This function allows one player to play
	 * a move given an x and y coordinate
	 */
	public void playMove() {
		if ((aiTurn == 1 && xTurn) || (aiTurn == -1 && !xTurn))
			playMoveAI();
		else {
			// if (getWinningMove(board)[0] == -1)
			// 	System.out.println("No Winning Move");
			Scanner keyboard = new Scanner(System.in);
			int playerX, playerY;
			do {
				do {
					System.out.print("Enter an X coordinate:\t");
					playerX = keyboard.nextInt();
				}	while (playerX < 0 || playerX >= board.length);

				do {
					System.out.print("Enter a Y coordinate:\t");
					playerY = keyboard.nextInt();
				}	while (playerY < 0 || playerY >= board[playerX].length);

				if (board[playerY][playerX] != ' ')
					System.out.println(playerX + " " + playerY + " is already occupied!");
			}	while (board[playerY][playerX] != ' ');

			playMove(playerY, playerX);
		}
	}

	/**
	 * Prints the result of the game
	 */
	public void printResult() {
		System.out.println("\nGame Over!!!");
		switch (gameResult(board)) {
			case -1:
				System.out.println("Circles won!");
				break;
			case 0:
				System.out.println("Tie game!");
				break;
			case 1:
				System.out.println("X's won!");
				break;
		}
		System.out.println("\n");
	}

	/**
	 * This function plays the best move
	 */
	public void playMoveAI() {
		// Duplicating the array is redundant with the way Depth First Search is implemented, but it is still a good habit
		char[][] boardCopy = new char[board.length][board[0].length];
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				boardCopy[i][a] = board[i][a];

		// Run many trials for the current root node
		if (monteCarloMode) {
			for (int i = 0; i < monteCarloTrials; i++)
				root.chooseChild();

			int[] bestMove = getBestMove(root);
			playMove(bestMove[0], bestMove[1]);
		}
		else {
			int[] aiAnalysis = alternateFindBestMove(boardCopy, xTurn);
			playMove(aiAnalysis[1], aiAnalysis[2]);
		}
		
	}

	/**
	 * Return a two dimesional list of possible moves,
	 * with the second dimension containing the x and y
	 * coordinates. Eg: a result of [[1, 2], [1, 1]]
	 * means that the only two possible moves are at
	 * board[1][2] and at board[1][1].
	 * 
	 * @param  board The current state of the board
	 * @return       A two dimensional array containing all the possible moves
	 */
	public static int[][] possibleMoves(char[][] board) {
		int numPossibleMoves = 0;
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ')
					numPossibleMoves++;

		int[][] possibleMoves = new int[numPossibleMoves][2];
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ') {
					possibleMoves[numPossibleMoves-1][0] = i;
					possibleMoves[numPossibleMoves-1][1] = a;
					numPossibleMoves--;
				}
		return possibleMoves;
	}

	/**
	 * This (recursive) function finds the best move, returning
	 * a result array with the analysis, the x, and the y coords
	 * for the best move
	 * 
	 * @param  board The current state of the board
	 * @param  xTurn Whether or not it is the X's turn currently
	 * @return       A result including the best x and y coords
	 */
	public int[] findBestMove(char[][] board, boolean xTurn) {

		// If the game is already over with this board, return the result

		if (gameOver(board))
			return new int[] {gameResult(board), -1, -1};

		// If the game is still going, check all the possible moves
		// and choose the one with the most favorable outcome for the player
		
		int[][] possibleMoves = possibleMoves(board);

		int bestX = possibleMoves[0][0], bestY = possibleMoves[0][1], result = xTurn ? -1:1;

		for (int i = 0; i < possibleMoves.length; i++) {
			// Place the move, then run the function recrusively, then undo the move
			board[possibleMoves[i][0]][possibleMoves[i][1]] = xTurn ? 'X':'O';
			int tempResult = findBestMove(board, !xTurn)[0];
			board[possibleMoves[i][0]][possibleMoves[i][1]] = ' ';

			// Check if the result is favorable for the player
			if ((xTurn && tempResult > result) || (!xTurn && tempResult < result)) {
				bestX = possibleMoves[i][0];
				bestY = possibleMoves[i][1];
				result = tempResult;
			}
			else if (tempResult == result && Math.random() > 1f / possibleMoves.length) { // element of randomness, optional
				bestX = possibleMoves[i][0];
				bestY = possibleMoves[i][1];
				result = tempResult;
			}
		}

		return new int[] {result, bestX, bestY};
	}

	/**
	 * This alternate implementation of DFS doesn't require a possibleMoves function.
	 * This makes it faster, and probably more intuitive
	 * @param  board The current state of the board
	 * @param  xTurn The current turn
	 * @return       A result
	 */
	public int[] alternateFindBestMove(char[][] board, boolean xTurn) {

		// If the game is already over with this board, return the result

		if (gameOver(board))
			return new int[] {gameResult(board), -1, -1};

		// If the game is still going, check all the possible moves
		// and choose the one with the most favorable outcome for the player
		
		int bestX = -1, bestY = -1, result = xTurn ? -1:1;

		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++) {
				if (board[i][a] != ' ')
					continue;
				// Place the move, then run the function recrusively, then undo the move
				board[i][a] = xTurn ? 'X':'O';
				int tempResult = alternateFindBestMove(board, !xTurn)[0];
				board[i][a] = ' ';

				// Check if the result is favorable for the player
				if ((xTurn && tempResult > result) || (!xTurn && tempResult < result)) {
					bestX = i;
					bestY = a;
					result = tempResult;
				}
				else if (tempResult == result && Math.random() > 0.5) { // element of randomness, optional
					bestX = i;
					bestY = a;
					result = tempResult;
				}
		}

		return new int[] {result, bestX, bestY};
	}

	/**
	 * This functions returns true if a win is possible
	 * in the current position, false otherwise. Note that this function
	 * is not used elsewhere in the program.
	 * @param  board The current state of the board
	 * @return       A boolean value true if possible, false otherwise
	 */
	public static boolean winPossible(char[][] board) {

		char color;
		int consecutive;
		boolean emptyAdjacentSpot;

		// check vertical
		for (int i = 0; i < board.length; i++) {
			consecutive = 0;
			color = '?';
			emptyAdjacentSpot = false;
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else emptyAdjacentSpot = true;
			if (consecutive == 3 || (consecutive == 2 && emptyAdjacentSpot))
				return true;
		}

		// check horizontal
		for (int a = 0; a < board[0].length; a++) {
			consecutive = 0;
			color = '?';
			emptyAdjacentSpot = false;
			for (int i = 0; i < board.length; i++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else emptyAdjacentSpot = true;
			if (consecutive == 3 || (consecutive == 2 && emptyAdjacentSpot))
				return true;
		}

		// check top-left to bottom-right diagonal
		consecutive = 0;
		color = '?';
		emptyAdjacentSpot = false;
		for (int i = 0, a = 0; i < board.length; i++, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else emptyAdjacentSpot = true;
		if (consecutive == 3 || (consecutive == 2 && emptyAdjacentSpot))
			return true;

		// check top-right to bottom-left diagonal
		consecutive = 0;
		color = '?';
		emptyAdjacentSpot = false;
		for (int i = board.length - 1, a = 0; i >= 0; i--, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else emptyAdjacentSpot = true;
		if (consecutive == 3 || (consecutive == 2 && emptyAdjacentSpot))
			return true;

		return false;
	}

	public static boolean identicalBoards(char[][] board1, char[][] board2) {
		boolean identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[i][board1[i].length - 1 - a]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[board1.length - 1 - i][a]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[board1.length - 1 - i][board1[i].length - 1 - a]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[a][i]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[a][board1.length - 1 - i]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[board1[i].length - 1 - a][board1.length - 1 - i]) {
					identical = false;
					break outer;
				}
		if (identical)
			return true;

		identical = true;
		outer:
		for (int i = 0; i < board1.length; i++)
			for (int a = 0; a < board1[i].length; a++)
				if (board1[i][a] != board2[board1[i].length - 1 - a][i]) {
					identical = false;
					break outer;
				}
		return identical;
	}

	/**
	 * This function finds all the children nodes for the node,
	 * And returns them as an array of Nodes
	 * @param  board  The current state of the board
	 * @param  xTurn  The current turn
	 * @param  parent The Node that is looking for children
	 * @return        An array of Nodes
	 */
	public static TicTacToeMCTSNode[] getChildrenNodes(char[][] board, boolean xTurn, TicTacToeMCTSNode parent) {
		ArrayList<TicTacToeMCTSNode> children = new ArrayList<TicTacToeMCTSNode>(board.length * board[0].length);

		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ') {
					board[i][a] = xTurn ? 'X':'O';
					// Creates a new node with the new board state, different turn, and so on.
					children.add((int)(Math.random() * children.size()), new TicTacToeMCTSNode(board, !xTurn, parent, new int[] {i, a}, expansionConstant));
					board[i][a] = ' ';
				}

		for (int i = 0; i < children.size() - 1; i++)
			for (int a = i + 1; a < children.size(); a++)
				if (identicalBoards(children.get(i).board, children.get(a).board)) {
					children.remove(a);
					a--;
				}

		return children.toArray(new TicTacToeMCTSNode[children.size()]);
	}

	/**
	 * If there is a move that is winning for a player, return it
	 * as an int array containing the x and y coordinates
	 * 
	 * @param  board The current state of the board
	 * @return       An array containing the x and y coords
	 */
	public static int[] getWinningMove(char[][] board) {

		char color;
		int consecutive;
		boolean emptyAdjacentSpot;
		int[] spotLoc = null;

		// check vertical
		for (int i = 0; i < board.length; i++) {
			consecutive = 0;
			color = '?';
			emptyAdjacentSpot = false;
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else {
					emptyAdjacentSpot = true;
					spotLoc = new int[] {i, a};
				}
			if (consecutive == 2 && emptyAdjacentSpot)
				return spotLoc;
		}

		// check horizontal
		for (int a = 0; a < board[0].length; a++) {
			consecutive = 0;
			color = '?';
			emptyAdjacentSpot = false;
			for (int i = 0; i < board.length; i++)
				if (board[i][a] == color)
					consecutive++;
				else if (board[i][a] == 'X' || board[i][a] == 'O') {
					consecutive = 1;
					color = board[i][a];
				}
				else {
					emptyAdjacentSpot = true;
					spotLoc = new int[] {i, a};
				}
			if (consecutive == 2 && emptyAdjacentSpot)
				return spotLoc;
		}

		// check top-left to bottom-right diagonal
		consecutive = 0;
		color = '?';
		emptyAdjacentSpot = false;
		for (int i = 0, a = 0; i < board.length; i++, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else {
				emptyAdjacentSpot = true;
				spotLoc = new int[] {i, a};
			}
		if (consecutive == 2 && emptyAdjacentSpot)
			return spotLoc;

		// check top-right to bottom-left diagonal
		consecutive = 0;
		color = '?';
		emptyAdjacentSpot = false;
		for (int i = board.length - 1, a = 0; i >= 0; i--, a++)
			if (board[i][a] == color)
				consecutive++;
			else if (board[i][a] == 'X' || board[i][a] == 'O') {
				consecutive = 1;
				color = board[i][a];
			}
			else {
				emptyAdjacentSpot = true;
				spotLoc = new int[] {i, a};
			}
		if (consecutive == 2 && emptyAdjacentSpot)
			return spotLoc;

		return new int[] {-1, -1};
	}

	/**
	 * Returns a random legal move, or a move that is winning
	 * 
	 * @param  board The current state of the board
	 * @return       A random move int he form of an int array [xcoord, ycoord]
	 */
	public static int[] getRandomMove(char[][] board, boolean xTurn) {
		// For a true Monte Carlo solution, comment out the next three lines
		// so that the simulations will truly be random.
		if (antiTicTacToe) {
			antixCoords = new ArrayList<Integer>(board.length * board[0].length);
			antiyCoords = new ArrayList<Integer>(board.length * board[0].length);
		}
		else {
			int[] victoryMove = getWinningMove(board);
			if (!antiTicTacToe && victoryMove[0] != -1)
				return victoryMove;
		}

		ArrayList<Integer> xCoords = new ArrayList<Integer>(board.length * board[0].length);
		ArrayList<Integer> yCoords = new ArrayList<Integer>(board.length * board[0].length);

		int numPossibleMoves = 0;
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ') {
					if (antiTicTacToe) {
						board[i][a] = xTurn ? 'X':'O';
						if (gameResult(board) == 0) {
							antixCoords.add(i);
							antiyCoords.add(a);
						}
						board[i][a] = ' ';
					}
					xCoords.add(i);
					yCoords.add(a);
				}

		if (antiTicTacToe && antixCoords.size() > 0) {
			int move = (int)(Math.random() * antixCoords.size());
			return new int[] {antixCoords.get(move), antiyCoords.get(move)};
		}
		else {
			int move = (int)(Math.random() * xCoords.size());
			return new int[] {xCoords.get(move), yCoords.get(move)};
		}
	}
	static ArrayList<Integer> antixCoords, antiyCoords;

	/**
	 * If the root doesn't exist, create the root
	 * If the root exists, change the root to the node in
	 * the root's subtree that played the corresponding x 
	 * and y.
	 * 
	 * @param  x The x coord played
	 * @param  y The y coord played
	 * @return   A new root
	 */
	public TicTacToeMCTSNode nextRoot(int x, int y) {
		if (root == null || root.children == null)
			return new TicTacToeMCTSNode(board, xTurn, null, null, expansionConstant);
		for (int i = 0; i < root.children.length; i++)
			if (root.children[i].lastMove[0] == x && root.children[i].lastMove[1] == y) {
				root = root.children[i];
				root.parent = null;
				return root;
			}

		return new TicTacToeMCTSNode(board, xTurn, null, null, expansionConstant);
	}

	/**
	 * Returns the move coords of the best move, determined by
	 * the child note with the greatest total trials (To understand
	 * this better, look up Monte Carlo Tree Search on Wikipedia).
	 * @param  root The root node
	 * @return      The move coords of the best move
	 */
	public static int[] getBestMove(TicTacToeMCTSNode root) {
		int[] bestMove = new int[2];
		int mostTrials = 0;
		for (int i = 0; i < root.children.length; i++) {
			if (root.children[i].totalTrials > mostTrials) {
				mostTrials = root.children[i].totalTrials;
				bestMove = root.children[i].lastMove;
			}
		}
		return bestMove;
	}
}

/**
 * This class acts as a single Monte Carlo Tic Tac Toe Node.
 * To understand this better, look up Monte Carlo Tree Search
 * on Wikipedia
 */
class TicTacToeMCTSNode {
	public char[][] board;
	public boolean xTurn;
	public TicTacToeMCTSNode parent;
	public int[] lastMove;
	public double expansionConstant;
	public int hits, misses, totalTrials;
	public TicTacToeMCTSNode[] children;

	TicTacToeMCTSNode(char[][] board, boolean xTurn, TicTacToeMCTSNode parent, int[] lastMove, double expansionConstant) {
		this.board = new char[board.length][board[0].length];
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				this.board[i][a] = board[i][a];

		this.xTurn = xTurn;
		this.parent = parent;
		this.lastMove = lastMove;
		this.expansionConstant = expansionConstant;
		hits = misses = totalTrials = 0;
	}

	/**
	 * Evaluates the child's potential
	 * @param  child The child node to evaluate
	 * @return       The evaluated potential
	 */
	private double childPotential(TicTacToeMCTSNode child) {
		// This formula can be found on Wikipedia
		double w = child.misses - child.hits, n = child.totalTrials;
		return w / n + expansionConstant * Math.sqrt(Math.log(totalTrials) / n);
	}

	/**
	 * Choose a child, run a simulation if needed, and then 
	 * backpropogate the results
	 */
	public void chooseChild() {
		// If the node does not have any children yet, give it children
		if (children == null)
			children = TicTacToePlus.getChildrenNodes(board, xTurn, this);
		// If the node's board represents a completed game, backpropogate the results
		if (TicTacToePlus.gameOver(board))
			backPropogate(TicTacToePlus.gameResult(board));
		else {
			int countUnexplored = 0;
			for (int i = 0; i < children.length; i++)
				if (children[i].totalTrials == 0)
					countUnexplored++;
			// If the node has any unexplored children, explore them
			if (countUnexplored > 0) {
				TicTacToeMCTSNode[] unexplored = new TicTacToeMCTSNode[countUnexplored];
				for (int i = 0; i < children.length; i++)
					if (children[i].totalTrials == 0) {
						countUnexplored--;
						unexplored[countUnexplored] = children[i];
					}
				// Run a simulation for a random unexplored child
				unexplored[(int)(Math.random() * unexplored.length)].runSimulation();
			}
			// If all the Node's children are explored, call this function in the child
			// with the best potential (look up on Wikipedia to understand potential)
			else {
				TicTacToeMCTSNode bestChild = null;
				double bestPotential = -1, potential;
				for (int i = 0; i < children.length; i++) {
					potential = childPotential(children[i]);
					if (potential > bestPotential) {
						bestPotential = potential;
						bestChild = children[i];
					}
				}
				bestChild.chooseChild();
			}	
		}
	}

	/**
	 * This function backpropogates a simulation result
	 * all the way to the root node.
	 * @param result The result of the simulation
	 */
	public void backPropogate(int result) {
		if ((result > 0 && xTurn) || (result < 0 && !xTurn))
			hits++;
		else if (result != 0)
			misses++;
		totalTrials++;
		if (parent != null)
			parent.backPropogate(result);
	}

	/**
	 * Run a single simulation for this node. Note that these simulations
	 * are not completely random since the getRandomMove function automatically
	 * returns winning or losing moves. This can be changed by commenting the first
	 * three lines
	 *
	 * @see getRandomMove
	 */
	public void runSimulation() {
		char[][] boardCopy = new char[board.length][board[0].length];
		for (int i = 0; i < boardCopy.length; i++)
			for (int a = 0; a < boardCopy[i].length; a++)
				boardCopy[i][a] = board[i][a];
		boolean turn = xTurn;
		while (!TicTacToePlus.gameOver(boardCopy)) {
			int[] move = TicTacToePlus.getRandomMove(boardCopy, turn);
			boardCopy[move[0]][move[1]] = turn ? 'X':'O';
			turn = !turn;
		}
		// Backpropogate the result of the simulation
		backPropogate(TicTacToePlus.gameResult(boardCopy));
	}
}
