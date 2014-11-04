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
	
	public GameMove getMove(GameState state, String lastMove)
	{
		minimax((ClobberState)state, 0);
		return mvStack[0];
	}
	
	public static void main(String [] args)
	{
		int depth = 5;
		GamePlayer p = new GeorgeClobberPlayer("George+", depth);
		p.compete(args, 1);
	}
}
