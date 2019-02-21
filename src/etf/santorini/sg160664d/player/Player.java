package etf.santorini.sg160664d.player;

import java.util.Collection;

import etf.santorini.sg160664d.board.Alliance;
import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Move;
import etf.santorini.sg160664d.board.Move.LegalMoves;
import etf.santorini.sg160664d.gui.Table;
import etf.santorini.sg160664d.pieces.Piece;

public abstract class Player {
	
	protected final Board board;
	protected final LegalMoves legalMoves;
	protected Table.PlayerType playerType;

	Player(final Board board,
			final LegalMoves legalMoves,
			final LegalMoves opponentMoves){
		this.board = board;
		this.legalMoves = legalMoves; //this players moves
	}

	public Table.PlayerType getPlayerType(){ return this.playerType;}
	public void setPlayerType(Table.PlayerType playerType){
		this.playerType = playerType;
	}

	public LegalMoves getLegalMoves() {return legalMoves;}
	
	/**
	 * When we move we transition to another board where the move is made,
	 * if the move is illegal, than we just return the current board 
	 * @param move
	 * @return
	 */
	public MoveTransition makePieceMove(final Move move) {
		Collection<Move> collMoves;
		if(!(move.getMovedPiece()== null)){
			collMoves = move.getMovedPiece().calculateLegalMoves(this.board);
		}else {
			collMoves = null;
		}
		if(!isMoveLegal((Move.MajorMove) move, collMoves)) {
			//Debugging
			System.out.println("\n ------ \n ERROR: Not legal Move");

			return new MoveTransition(this.board, move, MoveStatus.ILEGAL_MOVE);
		}else {
			//Debugging
			System.out.println("\n ------ \n YESSSS: not illegal Move");
		}
		final Board transitionBoard = move.execute();
		return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
	}
	
	public MoveTransition makeBuildMove(final Move move) {
		//Collection<Move> collMoves = move.getMovedPiece().calculateLegalBuildSpots(this.board);
		Collection<Move> collMoves;
		if(!(move.getMovedPiece()== null)){
			collMoves = move.getMovedPiece().calculateLegalBuildSpots(this.board);
		}else {
			collMoves = null;
		}
		if(!isBuildMoveLegal((Move.BuildMove) move,collMoves)){
			return new MoveTransition(this.board, move, MoveStatus.ILEGAL_MOVE);
		}
		final Board transitionBoard = move.execute();
		return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
	}
	/**
	 * Checking if a move is legal for the current player or not.
	 * @param move
	 * @return boolean,
	 */
	public boolean isMoveLegal(final Move move) {
		return this.legalMoves.isStandardLegalMove(move);
	}
//extra one
	public boolean isMoveLegal(final Move.MajorMove move, Collection<Move> standardLegalMoves){
		for(final Move move1: standardLegalMoves){
			if(move.getMovedPiece().getPiecePosition() == move1.getMovedPiece().getPiecePosition()  &&
					move.getDestinationCoordinate() == move1.getDestinationCoordinate()){
				return true;
			}
		}
		return false;
	}

	public boolean isBuildMoveLegal(final Move move) {
		return this.legalMoves.isLegalBuildMove(move);
	}
	public boolean isBuildMoveLegal(final Move.BuildMove move, Collection<Move> buildMovesColl){
		for(final Move move1: buildMovesColl){
			if(move.getMovedPiece().getPiecePosition() == move1.getMovedPiece().getPiecePosition()  &&
					move.getDestinationCoordinate() == move1.getDestinationCoordinate()){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Abstract methods
	 */
	public abstract Collection<Piece> getActivePieces();
	public abstract Alliance getAlliance();
	public abstract Player getOpponent();
	
}//!-Player
