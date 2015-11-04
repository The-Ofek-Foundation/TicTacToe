/**
 * An implementation of the game Tic Tac Toe
 * in Java, with an artificial intelligence using
 * depth first search.
 * 
 * @author Ofek Gila
 * @since September 3rd, 2015
 */

import java.util.Scanner;

public class TicTacToe {

	public char[][] board;
	public boolean xTurn;
	/**
	 * aiTurn is 0 for no ai, 1 for X and 0 for O
	 */
	public int aiTurn;

	public TicTacToe(int aiTurn) {
		this.aiTurn = aiTurn;
		xTurn = true;
		board = new char[3][3];
		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				board[i][a] = ' ';
	}

	public static void printBoard(char[][] board) {
		System.out.println();
		for (int i = 0; i < board.length; i++) {
			System.out.print("   " + board[i][0]);
			for (int a = 1; a < board[i].length; a++)
				System.out.print("|" + board[i][a]);
			System.out.println();
			System.out.print("   ");
			if (i < board.length - 1)
				System.out.print("-----");
			System.out.println(); 
		}
		System.out.println();
	}

	public static void main(String... pumpkins) {
		// Pass 0 for no ai, 1 for ai as X and -1 for ai as O
		TicTacToe TTT = new TicTacToe(-1);
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
	public int gameResult(char[][] board) {

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
			if (consecutive == 3)
				return color == 'X' ? 1:-1;
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
			if (consecutive == 3)
				return color == 'X' ? 1:-1;
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
		if (consecutive == 3)
			return color == 'X' ? 1:-1;

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
		if (consecutive == 3)
			return color == 'X' ? 1:-1;

		return 0;
	}

	/**
	 * This function returns true if the game is over,
	 * false otherwise
	 * 
	 * @param  board   The current state of the board
	 * @return boolean Whether or not game is over
	 */
	public boolean gameOver(char[][] board) {
		if (gameResult(board) != 0)
			return true;

		for (int i = 0; i < board.length; i++)
			for (int a = 0; a < board[i].length; a++)
				if (board[i][a] == ' ')
					return false;

		return true;
	}

	/**
	 * This function allows one player to play
	 * a move given an x and y coordinate
	 */
	public void playMove() {
		if ((aiTurn == 1 && xTurn) || (aiTurn == -1 && !xTurn))
			playMoveAI();
		else {
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

			board[playerY][playerX] = xTurn ? 'X':'O';
		}
		xTurn = !xTurn;
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

		int[] aiAnalysis = findBestMove(boardCopy, xTurn);
		board[aiAnalysis[1]][aiAnalysis[2]] = xTurn ? 'X':'O';
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
	public int[][] possibleMoves(char[][] board) {
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

		// printBoard(board);
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
	 * This functions returns true if a win is possible
	 * in the current position, false otherwise. Note that this function
	 * is not used elsewhere in the program.
	 * @param  board The current state of the board
	 * @return       A boolean value true if possible, false otherwise
	 */
	public boolean winPossible(char[][] board) {

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
}
