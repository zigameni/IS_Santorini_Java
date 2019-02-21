package etf.santorini.sg160664d.player;

import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Move;

public class MoveTransition {
	
	private final Board transitionBoard;
	private final Move move;
	private final MoveStatus moveStatus;
	
	public MoveTransition(final Board transitionBoard,
							final Move move,
							final MoveStatus moveStatus) {
		this.transitionBoard = transitionBoard;
		this.move = move;
		this.moveStatus = moveStatus;
	}
	public MoveStatus getMoveStatus() {return this.moveStatus;}

    public Board getTransitionBoard() {
		return transitionBoard;
    }
    public Move getMove(){return this.move;}
}//!- MoveTransition
