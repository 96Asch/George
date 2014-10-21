package clobber;
import game.*;

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
			
			

//			int [] columns = new int [COLS];
//			for (int j=0; j<COLS; j++) {
//				columns[j] = j;
//			}
//			
//			//shuffle(columns);
//			
//			for (int i=0; i<Connect4State.NUM_COLS; i++) {
//				int c = columns[i];
//				if (brd.numInCol[c] < Connect4State.NUM_ROWS) {
//					// Make move on board
//					tempMv.col = c;
//					brd.makeMove(tempMv);
//					
//					// Check out worth of this move
//					minimax(brd, currDepth+1);
//					
//					// Undo the move
//					brd.numInCol[c]--;
//					int row = brd.numInCol[c]; 
//					brd.board[row][c] = ClobberState.emptySym;
//					brd.numMoves--;
//					brd.status = GameState.Status.GAME_ON;
//					brd.who = currTurn;
//					
//					// Check out the results, relative to what we've seen before
//					if (toMaximize && nextMove.score > bestMove.score) {
//						bestMove.set(new ClobberMove(), nextMove.score);
//					} else if (!toMaximize && nextMove.score < bestMove.score) {
//						bestMove.set(new ClobberMove(), nextMove.score);
//					}
//				}
//			}
		}
	}
	
	public double evalBoard(ClobberState brd){
		double score = 0;
		
		return score;
	}
	
	public GameMove getMove(GameState state, String lastMove)
	{
		minimax((ClobberState)state, 0);
		return mvStack[0];
	}
	
	public static void main(String [] args)
	{
		int depth = 6;
		GamePlayer p = new GeorgeClobberPlayer("George+", depth);
		p.compete(args, 1);
	}
}
