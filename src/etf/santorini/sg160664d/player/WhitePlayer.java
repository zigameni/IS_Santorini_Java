package etf.santorini.sg160664d.player;

import java.util.Collection;

import etf.santorini.sg160664d.board.Alliance;
import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Move.LegalMoves;
import etf.santorini.sg160664d.gui.Table;
import etf.santorini.sg160664d.pieces.Piece;

public class WhitePlayer extends Player {

	
	public WhitePlayer(Board board, LegalMoves whiteLegalMoves, LegalMoves blackLegalMoves) {
		super(board, whiteLegalMoves, blackLegalMoves);
		this.playerType = Table.PlayerType.HUMAN;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Collection<Piece> getActivePieces() {
		return board.getWhitePieces();
	}

	@Override
	public Alliance getAlliance() {
		return Alliance.WHITE;
	}

	@Override
	public Player getOpponent() {
		return this.board.getBlackPlayer();
	}

	
	
	
	
}//!- WhitePlayer
