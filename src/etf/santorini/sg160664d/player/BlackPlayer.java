package etf.santorini.sg160664d.player;

import java.util.Collection;

import etf.santorini.sg160664d.board.Alliance;
import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Move.LegalMoves;
import etf.santorini.sg160664d.gui.Table;
import etf.santorini.sg160664d.pieces.Piece;

public class BlackPlayer extends Player {


	public BlackPlayer(Board board, LegalMoves whiteLegalMoves, LegalMoves blackLegalMoves) {
		super(board, blackLegalMoves, whiteLegalMoves);
		this.playerType = Table.PlayerType.HUMAN;
	}

	public Collection<Piece> getActivePieces(){
		return board.getBlackPieces();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.BLACK;
	}

	@Override
	public Player getOpponent() {
		return this.board.getWhitePlayer();
	}
	
	

}//!- BlackPlayer
