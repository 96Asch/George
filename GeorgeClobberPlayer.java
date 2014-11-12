package clobber;
import game.*;
import game.GameState.Who;
import java.util.*;

public class GeorgeClobberPlayer extends GamePlayer {
	public final int MAX_DEPTH = 50;
	public int depthLimit;
	public final int MAX_SCORE = 100000;
	
	public ArrayList<ScoredClobberMove> possibleMoves;
	public ArrayList<ScoredClobberMove> moves;
	
	protected ScoredClobberMove [] mvStack;
	
	protected class ScoredClobberMove extends ClobberMove {
		public ScoredClobberMove(ClobberMove m, double s)
		{
			super(m);
			score = s;
		}
		public void set(ClobberMove m, double s)
		{
			row1 = m.row1;
			col1 = m.col1;
			row2 = m.row2;
			col2 = m.col2;
			score = s;
		}
		public double score;
	}
	
	protected class AlphaBetaThread implements Runnable{
		protected ScoredClobberMove[] mStack;
		protected ClobberState brd;
		
		public AlphaBetaThread(ClobberState state){
			brd = state;
		}
		
		public void run(){
			mStack = new ScoredClobberMove [MAX_DEPTH];
			for (int i=0; i<MAX_DEPTH; i++) {
				mStack[i] = new ScoredClobberMove(new ClobberMove(),0);
			}
			boolean done = false;
			while(!done){
				ScoredClobberMove move = getMove();
				if(move == null){
					done = true;
					return;
				}
				ClobberState board = (ClobberState)brd.clone();
				board.makeMove(move);
				alphaBeta(board, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
				setPossibleScore(mStack[0]);
			}
		}
		
		private void alphaBeta(ClobberState brd, int currDepth, double alpha, double beta)
		{
			boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
			boolean toMinimize = !toMaximize;
			boolean isTerminal = terminalValue(brd, mStack[currDepth]);
			
			if (isTerminal) {
				;
			} else if (currDepth == depthLimit) {
				mStack[currDepth].set(new ClobberMove(), evalBoard(brd));
			} else {
				ScoredClobberMove tempMv = new ScoredClobberMove(new ClobberMove(), 0);

				double bestScore = (toMaximize ? Double.NEGATIVE_INFINITY :
													Double.POSITIVE_INFINITY);
				ScoredClobberMove bestMove = mStack[currDepth];
				ScoredClobberMove nextMove = mStack[currDepth+1];
				
				bestMove.set(new ClobberMove(), bestScore);
				GameState.Who currTurn = brd.getWho();
				char mySymbol;
				char oppSymbol;
				
				if(currTurn == Who.AWAY){
					mySymbol = ClobberState.awaySym;
					oppSymbol = ClobberState.homeSym;
				}
				else {
					mySymbol = ClobberState.homeSym;
					oppSymbol = ClobberState.awaySym;
				}
				
				for(int i = 0; i < ClobberState.ROWS; i++){
					for(int j = 0; j < ClobberState.COLS; j++){
						if(brd.board[i][j] == mySymbol){
							if(i > 0 && brd.board[i-1][j] == oppSymbol){
								tempMv.row1 = i;
								tempMv.col1 = j;
								tempMv.row2 = i - 1;
								tempMv.col2 = j;
								brd.makeMove(tempMv);

								alphaBeta(brd, currDepth+1, alpha, beta);
								
								//Undo move
								brd.board[i][j] = mySymbol;
								brd.board[i-1][j] = oppSymbol;
								brd.numMoves--;
								brd.status = GameState.Status.GAME_ON;
								brd.who = currTurn;

								//Check Results
								if (toMaximize && nextMove.score > bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								}
								
								// Update alpha and beta. Perform pruning, if possible.
								if (toMinimize) {
									beta = Math.min(bestMove.score, beta);
									if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
										return;
									}
								} else {
									alpha = Math.max(bestMove.score, alpha);
									if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
										return;
									}
								}
							}
							if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
								tempMv.row1 = i;
								tempMv.col1 = j;
								tempMv.row2 = i + 1;
								tempMv.col2 = j;
								brd.makeMove(tempMv);

								alphaBeta(brd, currDepth+1, alpha, beta);

								//Undo move
								brd.board[i][j] = mySymbol;
								brd.board[i+1][j] = oppSymbol;
								brd.numMoves--;
								brd.status = GameState.Status.GAME_ON;
								brd.who = currTurn;

								//Check Results
								if (toMaximize && nextMove.score > bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								}
								
								// Update alpha and beta. Perform pruning, if possible.
								if (toMinimize) {
									beta = Math.min(bestMove.score, beta);
									if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
										return;
									}
								} else {
									alpha = Math.max(bestMove.score, alpha);
									if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
										return;
									}
								}
							}
							if(j > 0 && brd.board[i][j-1] == oppSymbol){
								tempMv.row1 = i;
								tempMv.col1 = j;
								tempMv.row2 = i;
								tempMv.col2 = j - 1;
								brd.makeMove(tempMv);

								alphaBeta(brd, currDepth+1, alpha, beta);
								
								//Undo move
								brd.board[i][j] = mySymbol;
								brd.board[i][j-1] = oppSymbol;
								brd.numMoves--;
								brd.status = GameState.Status.GAME_ON;
								brd.who = currTurn;

								//Check Results
								if (toMaximize && nextMove.score > bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								}
								
								// Update alpha and beta. Perform pruning, if possible.
								if (toMinimize) {
									beta = Math.min(bestMove.score, beta);
									if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
										return;
									}
								} else {
									alpha = Math.max(bestMove.score, alpha);
									if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
										return;
									}
								}
							}
							if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
								tempMv.row1 = i;
								tempMv.col1 = j;
								tempMv.row2 = i;
								tempMv.col2 = j + 1;
								brd.makeMove(tempMv);

								alphaBeta(brd, currDepth+1, alpha, beta);
								
								//Undo move
								brd.board[i][j] = mySymbol;
								brd.board[i][j+1] = oppSymbol;
								brd.numMoves--;
								brd.status = GameState.Status.GAME_ON;
								brd.who = currTurn;

								//Check Results
								if (toMaximize && nextMove.score > bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								} else if (!toMaximize && nextMove.score < bestMove.score) {
									bestMove.set(tempMv, nextMove.score);
								}
								
								// Update alpha and beta. Perform pruning, if possible.
								if (toMinimize) {
									beta = Math.min(bestMove.score, beta);
									if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
										return;
									}
								} else {
									alpha = Math.max(bestMove.score, alpha);
									if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public synchronized void setPossibleScore(ScoredClobberMove move){
		possibleMoves.add(move);
	}
	
	public synchronized ScoredClobberMove getMove(){
		if(moves.size() != 0){
			ScoredClobberMove move = moves.get(0);
			moves.remove(0);
			return move;
		}
		return null;
	}
	
	public GeorgeClobberPlayer(String n, int d) 
	{
		super(n, new ClobberState(), false);
		depthLimit = d;
	}
	
//	Shuffles the nodes for random node arrangement.
//	
//	protected static void shuffle(int [] ary)
//	{
//		int len = ary.length;
//		for (int i=0; i<len; i++) {
//			int spot = Util.randInt(i, len-1);
//			int tmp = ary[i];
//			ary[i] = ary[spot];
//			ary[spot] = tmp;
//		}
//	}
	
	public void init()
	{
		mvStack = new ScoredClobberMove [MAX_DEPTH];
		for (int i=0; i<MAX_DEPTH; i++) {
			mvStack[i] = new ScoredClobberMove(new ClobberMove(),0);
		}
	}

	protected boolean terminalValue(GameState brd, ScoredClobberMove mv)
	{
		GameState.Status status = brd.getStatus();
		boolean isTerminal = true;
		
		if (status == GameState.Status.HOME_WIN) {
			mv.set(new ClobberMove(), MAX_SCORE);
		} else if (status == GameState.Status.AWAY_WIN) {
			mv.set(new ClobberMove(), -MAX_SCORE);
		} else {
			isTerminal = false;
		}
		return isTerminal;
	}
	
	private void alphaBeta(ClobberState brd, int currDepth, double alpha, double beta)
	{
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;
		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);
		
		if (isTerminal) {
			;
		} else if (currDepth == depthLimit) {
			mvStack[currDepth].set(new ClobberMove(), evalBoard(brd));
		} else {
			ScoredClobberMove tempMv = new ScoredClobberMove(new ClobberMove(), 0);

			double bestScore = (toMaximize ? Double.NEGATIVE_INFINITY :
												Double.POSITIVE_INFINITY);
			ScoredClobberMove bestMove = mvStack[currDepth];
			ScoredClobberMove nextMove = mvStack[currDepth+1];
			
			bestMove.set(new ClobberMove(), bestScore);
			GameState.Who currTurn = brd.getWho();
			char mySymbol;
			char oppSymbol;
			
			if(currTurn == Who.AWAY){
				mySymbol = ClobberState.awaySym;
				oppSymbol = ClobberState.homeSym;
			}
			else {
				mySymbol = ClobberState.homeSym;
				oppSymbol = ClobberState.awaySym;
			}
			
			for(int i = 0; i < ClobberState.ROWS; i++){
				for(int j = 0; j < ClobberState.COLS; j++){
					if(brd.board[i][j] == mySymbol){
						if(i > 0 && brd.board[i-1][j] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i - 1;
							tempMv.col2 = j;
							brd.makeMove(tempMv);

							alphaBeta(brd, currDepth+1, alpha, beta);
							
							//Undo move
							brd.board[i][j] = mySymbol;
							brd.board[i-1][j] = oppSymbol;
							brd.numMoves--;
							brd.status = GameState.Status.GAME_ON;
							brd.who = currTurn;

							//Check Results
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							} else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							}
							
							// Update alpha and beta. Perform pruning, if possible.
							if (toMinimize) {
								beta = Math.min(bestMove.score, beta);
								if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
									return;
								}
							} else {
								alpha = Math.max(bestMove.score, alpha);
								if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
									return;
								}
							}
						}
						if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i + 1;
							tempMv.col2 = j;
							brd.makeMove(tempMv);

							alphaBeta(brd, currDepth+1, alpha, beta);

							//Undo move
							brd.board[i][j] = mySymbol;
							brd.board[i+1][j] = oppSymbol;
							brd.numMoves--;
							brd.status = GameState.Status.GAME_ON;
							brd.who = currTurn;

							//Check Results
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							} else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							}
							
							// Update alpha and beta. Perform pruning, if possible.
							if (toMinimize) {
								beta = Math.min(bestMove.score, beta);
								if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
									return;
								}
							} else {
								alpha = Math.max(bestMove.score, alpha);
								if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
									return;
								}
							}
						}
						if(j > 0 && brd.board[i][j-1] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i;
							tempMv.col2 = j - 1;
							brd.makeMove(tempMv);

							alphaBeta(brd, currDepth+1, alpha, beta);
							
							//Undo move
							brd.board[i][j] = mySymbol;
							brd.board[i][j-1] = oppSymbol;
							brd.numMoves--;
							brd.status = GameState.Status.GAME_ON;
							brd.who = currTurn;

							//Check Results
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							} else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							}
							
							// Update alpha and beta. Perform pruning, if possible.
							if (toMinimize) {
								beta = Math.min(bestMove.score, beta);
								if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
									return;
								}
							} else {
								alpha = Math.max(bestMove.score, alpha);
								if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
									return;
								}
							}
						}
						if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i;
							tempMv.col2 = j + 1;
							brd.makeMove(tempMv);

							alphaBeta(brd, currDepth+1, alpha, beta);
							
							//Undo move
							brd.board[i][j] = mySymbol;
							brd.board[i][j+1] = oppSymbol;
							brd.numMoves--;
							brd.status = GameState.Status.GAME_ON;
							brd.who = currTurn;

							//Check Results
							if (toMaximize && nextMove.score > bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							} else if (!toMaximize && nextMove.score < bestMove.score) {
								bestMove.set(tempMv, nextMove.score);
							}
							
							// Update alpha and beta. Perform pruning, if possible.
							if (toMinimize) {
								beta = Math.min(bestMove.score, beta);
								if (bestMove.score <= alpha || bestMove.score == -MAX_SCORE) {
									return;
								}
							} else {
								alpha = Math.max(bestMove.score, alpha);
								if (bestMove.score >= beta || bestMove.score == MAX_SCORE) {
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private void alphaBetaThreads(ClobberState brd, int currDepth){
		GameState.Who currTurn = brd.getWho();
		char mySymbol;
		char oppSymbol;
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		ScoredClobberMove tempMv;
		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == mySymbol){
					if(i > 0 && brd.board[i-1][j] == oppSymbol){
						tempMv = new ScoredClobberMove(new ClobberMove(), 0);
						tempMv.row1 = i;
						tempMv.col1 = j;
						tempMv.row2 = i - 1;
						tempMv.col2 = j;
						moves.add((ScoredClobberMove)tempMv.clone());
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
						tempMv = new ScoredClobberMove(new ClobberMove(), 0);
						tempMv.row1 = i;
						tempMv.col1 = j;
						tempMv.row2 = i + 1;
						tempMv.col2 = j;
						moves.add((ScoredClobberMove)tempMv.clone());
					}
					if(j > 0 && brd.board[i][j-1] == oppSymbol){
						tempMv = new ScoredClobberMove(new ClobberMove(), 0);
						tempMv.row1 = i;
						tempMv.col1 = j;
						tempMv.row2 = i;
						tempMv.col2 = j - 1;	
						moves.add((ScoredClobberMove)tempMv.clone());
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
						tempMv = new ScoredClobberMove(new ClobberMove(), 0);
						tempMv.row1 = i;
						tempMv.col1 = j;
						tempMv.row2 = i;
						tempMv.col2 = j + 1;
						moves.add((ScoredClobberMove)tempMv.clone());
					}
				}
			}
		}
		Thread[] threads = new Thread[5];
		for(int i = 0; i < 5; i++){
			threads[i] = new Thread(new AlphaBetaThread(brd));
			threads[i].run();
		}
		for(int i = 0; i < 5; i++){
			try{
				threads[i].join();
			} catch (Exception e){
				System.out.println("Error!/n" + e.getMessage());
			}
		}
		double bestScore = Double.NEGATIVE_INFINITY;
		ScoredClobberMove nextMove = null;
		for(int i = 0; i < possibleMoves.size(); i++){
			double score = possibleMoves.get(i).score;
			if(score > bestScore){
				bestScore = score;
				nextMove = possibleMoves.get(i);
			}
		}
		if(nextMove != null){
			mvStack[0] = nextMove;
		} else {
			System.err.println("Error, null move after alphabeta move check!");
		}
	}
	
	/**
	 * Isolate the opponent's pieces 
	 * TODO: Not yet finished
	 * @param brd
	 * @return
	 */
	public double heuristic1(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == oppSymbol){
					//add 1 for each move that cannot be made
					if(i > 0 && (brd.board[i-1][j] == oppSymbol || brd.board[i-1][j] == ClobberState.emptySym)){
						score++;
					}
					if(i < ClobberState.ROWS -1 && (brd.board[i+1][j] == oppSymbol || brd.board[i+1][j] == ClobberState.emptySym)){
						score++;
					}
					if(j > 0 && (brd.board[i][j-1] == oppSymbol || brd.board[i][j-1] == ClobberState.emptySym)){
						score++;
					}
					if(j < ClobberState.COLS -1 && (brd.board[i][j+1] == oppSymbol || brd.board[i][j+1] == ClobberState.emptySym)){
						score++;
					}
					//subtract 1 for each move that can be made by the opponent
					if(i > 0 && brd.board[i-1][j] == mySymbol){
						score--;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == mySymbol){
						score--;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol){
						score--;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == mySymbol){
						score--;
					}
				}
			}
		}
		return score;
	}

	/**
	 * If one of George's pieces is touching another of george's pieces and an opponent's piece add 1
	 * TODO: not yet finished
	 * @param brd
	 * @return
	 */
	public double heuristic2(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == mySymbol){
					boolean touchingFriend=false;
					boolean touchingOpponent=false;
					//Note: we can modify this to add up the number of touching opponent and friendly pieces instead and do a score based on that
					if(i > 0 && brd.board[i-1][j] == mySymbol){
						touchingFriend=true;
					}
					if(i > 0 && brd.board[i-1][j] == oppSymbol){
						touchingOpponent=true;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == mySymbol){
						touchingFriend=true;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
						touchingOpponent=true;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol){
						touchingFriend=true;
					}
					if(j > 0 && brd.board[i][j-1] == oppSymbol){
						touchingOpponent=true;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == mySymbol){
						touchingFriend=true;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
						touchingOpponent=true;
					}
					//Opponent is away score
					if(touchingOpponent&&touchingFriend){
						score+=5.0;//suggested that this be weighted more heavily than just 1
					}
				}
			}
		}
		return score;
	}
	
	/**
	 * Heuristic "3" appears to have been done in evalboard
	 * This and the remaining heuristics will be off by one
	 * This is heuristic 4: center board control, add up number of your pieces in the center:
	 * rows: 1,2,3,4 cols: 1,2,3
	 * TODO: need to do 
	 * @param brd
	 * @return
	 */
	public double heuristic3(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			if(i<5&&i>0){
				for(int j = 0; j < ClobberState.COLS; j++){
					if(j<4 && j>0 && brd.board[i][j] == mySymbol){
						score++;
					}
				}
			}
			
		}
		return score;
	}
	
	/**
	 * Heuristic "5": Edge Control use rows 0, 1, 4, 5 and cols: 0,1,3,4
	 * @param brd
	 * @return
	 */
	public double heuristic4(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			if(i<2||i>3){
				for(int j = 0; j < ClobberState.COLS; j++){
					if(j!=2 && brd.board[i][j] == mySymbol){
						score++;
					}
				}
			}
			
		}
		return score;
	}
	
	/**
	 * Heuristic "6" number of our pieces that can move
	 * TODO: consider scoring issue in heuristic ideas.txt
	 * @param brd
	 * @return
	 */
	public double heuristic5(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == mySymbol){
					boolean touchingOpponent=false;
					if(i > 0 && brd.board[i-1][j] == oppSymbol){
						touchingOpponent=true;
					}					
					if(!touchingOpponent && i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
						touchingOpponent=true;
					}
					if(!touchingOpponent && j > 0 && brd.board[i][j-1] == oppSymbol){
						touchingOpponent=true;
					}
					if(!touchingOpponent && j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
						touchingOpponent=true;
					}
					if(touchingOpponent){
						score++;   //increment score only by one, verified that this piece can move
					}
				}
			}
		}
		return score;
	}
	
	/**
	 * Heuristic "7" number of their pieces that can move
	 * TODO: consider scoring issue in heuristic ideas.txt
	 * @param brd
	 * @return
	 */
	public double heuristic6(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}
		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == oppSymbol){
					boolean touchingOpponent=false;
					if(i > 0 && brd.board[i-1][j] == mySymbol){
						touchingOpponent=true;
					}					
					if(!touchingOpponent && i < ClobberState.ROWS -1 && brd.board[i+1][j] == mySymbol){
						touchingOpponent=true;
					}
					if(!touchingOpponent && j > 0 && brd.board[i][j-1] == mySymbol){
						touchingOpponent=true;
					}
					if(!touchingOpponent && j < ClobberState.COLS -1 && brd.board[i][j+1] == mySymbol){
						touchingOpponent=true;
					}
					//Opponent is away score
					if(touchingOpponent){
						score++;   //increment score only by one, verified that this piece can move
					}
				}
			}
		}
		return score;
	}
	
	/**
	 * Num our pieces that can move - num their pieces that can move
	 * TODO: Consider question posed in heuristicIdeas.txt
	 * @param brd
	 * @return
	 */
	public double heuristic7(ClobberState brd){
		return heuristic5(brd)-heuristic6(brd);
	}
	
	/**
	 * This is really heuristic 3
	 * @param brd
	 * @return
	 */
	public double heuristic8(ClobberState brd){
		double score = 0;
		char mySymbol;
		char oppSymbol;
		GameState.Who currTurn = brd.getWho();
		
		if(currTurn == Who.AWAY){
			mySymbol = ClobberState.awaySym;
			oppSymbol = ClobberState.homeSym;
		}
		else {
			mySymbol = ClobberState.homeSym;
			oppSymbol = ClobberState.awaySym;
		}

		for(int i = 0; i < ClobberState.ROWS; i++){
			for(int j = 0; j < ClobberState.COLS; j++){
				if(brd.board[i][j] == mySymbol){
					//George is home score
					if(i > 0 && brd.board[i-1][j] == mySymbol && mySymbol == ClobberState.homeSym){
						score++;
					}
					if(i < ClobberState.ROWS - 1 && brd.board[i+1][j] == mySymbol && mySymbol == ClobberState.homeSym){
						score++;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol && mySymbol == ClobberState.homeSym){
						score++;
					}
					if(j < ClobberState.COLS - 1 && brd.board[i][j+1] == mySymbol  && mySymbol == ClobberState.homeSym){
						score++;
					}
					//George is away score
					if(i > 0 && brd.board[i-1][j] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(i < ClobberState.ROWS - 1 && brd.board[i+1][j] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(j < ClobberState.COLS - 1 && brd.board[i][j+1] == mySymbol  && mySymbol == ClobberState.awaySym){
						score--;
					}
				}
				
				if(brd.board[i][j] == oppSymbol){
					//Opponent is home score
					if(i > 0 && brd.board[i-1][j] == oppSymbol && oppSymbol == ClobberState.homeSym){
						score++;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol && oppSymbol == ClobberState.homeSym){
						score++;
					}
					if(j > 0 && brd.board[i][j-1] == oppSymbol && oppSymbol == ClobberState.homeSym){
						score++;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol  && oppSymbol == ClobberState.homeSym){
						score++;
					}
					//Opponent is away score
					if(i > 0 && brd.board[i-1][j] == oppSymbol && oppSymbol == ClobberState.awaySym){
						score--;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol && oppSymbol == ClobberState.awaySym){
						score--;
					}
					if(j > 0 && brd.board[i][j-1] == oppSymbol && oppSymbol == ClobberState.awaySym){
						score--;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol  && oppSymbol == ClobberState.awaySym){
						score--;
					}
				}
			}
		}
		
		return score;
	}
	
	public void openBook(ClobberState state){
		
	}
	
	public double evalBoard(ClobberState brd){
		double score = 0;
		
		//score += heuristic1(brd);
		//score += heuristic2(brd);
		score += heuristic3(brd);
		//score += heuristic4(brd);
		//score += heuristic5(brd);
		//score += heuristic6(brd);
		//score += heuristic7(brd);
		//score += heuristic8(brd);
		
		return score;
	}
	
	public GameMove getMove(GameState state, String lastMove)
	{
		possibleMoves = new ArrayList<ScoredClobberMove>();
		moves = new ArrayList<ScoredClobberMove>();
		//openBook((ClobberState)state);
		alphaBeta((ClobberState)state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		//alphaBetaThreads((ClobberState)state, 0);
		return mvStack[0];
	}
	
	public static void main(String [] args)
	{
		int depth = 12;
		GamePlayer p = new GeorgeClobberPlayer("George+", depth);
		p.compete(args, 1);
	}
}
