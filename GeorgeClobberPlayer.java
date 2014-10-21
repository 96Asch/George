package clobber;
import connect4.Connect4State;
import connect4.MiniMaxConnect4Player.ScoredConnect4Move;
import game.*;

public class GeorgeClobberPlayer extends GamePlayer {
	public final int MAX_DEPTH = 50;
	public int depthLimit;
	
	protected ScoredClobberMove [] mvStack;
	
	protected class ScoredClobberMove extends ClobberMove {
		public ScoredClobberMove(ClobberMove m, double s)
		{
			super(m);
			score = s;
		}
		public void set(ClobberMove m, double s)
		{
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
//	
//	public void init()
//	{
//		mvStack = new ScoredClobberMove [MAX_DEPTH];
//		for (int i=0; i<MAX_DEPTH; i++) {
//			mvStack[i] = new ScoredClobberMove(,0);
//		}
//	}
//	
//	protected boolean terminalValue(GameState brd, ScoredClobberMove mv)
//	{
//		GameState.Status status = brd.getStatus();
//		boolean isTerminal = true;
//		
//		return isTerminal;
//	}
//	
//	private void minimax(Connect4State brd, int currDepth)
//	{
//		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
//		boolean isTerminal = terminalValue(brd, mvStack[currDepth]);
//		
//		if (isTerminal) {
//			;
//		} else if (currDepth == depthLimit) {
//			mvStack[currDepth].set(0, evalBoard(brd));
//		} else {
//			ScoredConnect4Move tempMv = new ScoredConnect4Move(0, 0);
//
//			double bestScore = (brd.getWho() == GameState.Who.HOME ? 
//												Double.NEGATIVE_INFINITY :
//												Double.POSITIVE_INFINITY);
//			ScoredConnect4Move bestMove = mvStack[currDepth];
//			ScoredConnect4Move nextMove = mvStack[currDepth+1];
//			
//			bestMove.set(0, bestScore);
//			GameState.Who currTurn = brd.getWho();
//
//			int [] columns = new int [COLS];
//			for (int j=0; j<COLS; j++) {
//				columns[j] = j;
//			}
//			shuffle(columns);
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
//					brd.board[row][c] = Connect4State.emptySym;
//					brd.numMoves--;
//					brd.status = GameState.Status.GAME_ON;
//					brd.who = currTurn;
//					
//					// Check out the results, relative to what we've seen before
//					if (toMaximize && nextMove.score > bestMove.score) {
//						bestMove.set(c, nextMove.score);
//					} else if (!toMaximize && nextMove.score < bestMove.score) {
//						bestMove.set(c, nextMove.score);
//					}
//				}
//			}
//		}
//	}
	
	public GameMove getMove(GameState state, String lastMove)
	{
		//Need to change minimax to work with our game
		//minimax((Connect4State)brd, 0);
		return mvStack[0];
	}
	public static void main(String [] args)
	{
		int depth = 6;
		GamePlayer p = new GeorgeClobberPlayer("George+", depth);
		p.compete(args, 1);
	}
}
