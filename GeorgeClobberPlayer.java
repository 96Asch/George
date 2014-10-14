package clobber;
import game.*;

public class GeorgeClobberPlayer extends GamePlayer {
	public GeorgeClobberPlayer(String n) 
	{
		super(n, new ClobberState(), false);
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		ClobberState board = (ClobberState)state;  
		ClobberMove mv = new ClobberMove();
		
		return mv;
	}
	public static void main(String [] args)
	{
		GamePlayer p = new GeorgeClobberPlayer("George+");
		p.compete(args, 1);
	}
}
