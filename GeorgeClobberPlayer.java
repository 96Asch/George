package clobber;
import game.*;
import game.GameState.Who;

public class GeorgeClobberPlayer extends GamePlayer {
	public final int MAX_DEPTH = 50;
	public int depthLimit;
	public final int MAX_SCORE = 100000;
	
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

	private void minimax(ClobberState brd, int currDepth)
	{
		brd.toString();
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);
		
		if (isTerminal) {
			;
		} else if (currDepth == depthLimit) {
			mvStack[currDepth].set(new ClobberMove(), evalBoard(brd));
		} else {
			ScoredClobberMove tempMv = new ScoredClobberMove(new ClobberMove(), 0);

			double bestScore = (brd.getWho() == GameState.Who.HOME ? 
												Double.NEGATIVE_INFINITY :
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

							minimax(brd, currDepth+1);
							
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
						}
						if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i + 1;
							tempMv.col2 = j;
							brd.makeMove(tempMv);

							minimax(brd, currDepth+1);

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
						}
						if(j > 0 && brd.board[i][j-1] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i;
							tempMv.col2 = j - 1;
							brd.makeMove(tempMv);

							minimax(brd, currDepth+1);
							
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
						}
						if(j < ClobberState.COLS -1 && brd.board[i][j+1] == oppSymbol){
							tempMv.row1 = i;
							tempMv.col1 = j;
							tempMv.row2 = i;
							tempMv.col2 = j + 1;
							brd.makeMove(tempMv);

							minimax(brd, currDepth+1);
							
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
						}
					}
				}
			}
		}
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
	
	public double evalBoard(ClobberState brd){
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
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == mySymbol && mySymbol == ClobberState.homeSym){
						score++;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol && mySymbol == ClobberState.homeSym){
						score++;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == mySymbol  && mySymbol == ClobberState.homeSym){
						score++;
					}
					//George is away score
					if(i > 0 && brd.board[i-1][j] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(i < ClobberState.ROWS -1 && brd.board[i+1][j] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(j > 0 && brd.board[i][j-1] == mySymbol && mySymbol == ClobberState.awaySym){
						score--;
					}
					if(j < ClobberState.COLS -1 && brd.board[i][j+1] == mySymbol  && mySymbol == ClobberState.awaySym){
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
					if(i < ClobberState.ROWS -1 && (brd.board[i+1][j] == oppSymbol || brd.board[i-1][j] == ClobberState.emptySym)){
						score++;
					}
					if(j > 0 && (brd.board[i][j-1] == oppSymbol || brd.board[i-1][j] == ClobberState.emptySym)){
						score++;
					}
					if(j < ClobberState.COLS -1 && (brd.board[i][j+1] == oppSymbol || brd.board[i-1][j] == ClobberState.emptySym)){
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
	 * Count up number of pieces George has that are adjacent to each other, one for each adjacency
	 * TODO: Done, this is done in Eval board
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
			for(int j = 0; j < ClobberState.COLS; j++){
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
	
	//TODO: add remaining heuristic ideas
	
	public GameMove getMove(GameState state, String lastMove)
	{
		alphaBeta((ClobberState)state, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		return mvStack[0];
	}
	
	public static void main(String [] args)
	{
		int depth = 5;
		GamePlayer p = new GeorgeClobberPlayer("George+", depth);
		p.compete(args, 1);
	}
}
