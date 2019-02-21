package etf.santorini.sg160664d.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import etf.santorini.sg160664d.board.Alliance;
import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.BoardUtils;
import etf.santorini.sg160664d.board.Move;
import etf.santorini.sg160664d.board.Move.MajorMove;
import etf.santorini.sg160664d.board.Tile;

public class Piece {
	
	private final int piecePosition;
	private final Alliance pieceAlliance; //enum White, Black
	
	private final static int [] CANDIDATE_MOVE_COORDINATE = {-6, -5, -4, -1, 1, 4, 5, 6};
	
	public Piece(final int piecePositon, final Alliance pieceAlliance){
		this.pieceAlliance =pieceAlliance;
		this.piecePosition = piecePositon;
		//TODO More work here
	}

	//CopyConstructor
	public Piece(Piece piece) {
		this.piecePosition = piece.getPiecePosition();
		this.pieceAlliance = piece.getPieceAlliance();
	}

	//get the position of the piece
	public int getPiecePosition() {return this.piecePosition;}
	
	//get the alliance of the piece //white or black
	public Alliance getPieceAlliance() {return this.pieceAlliance;}
	
	/**
	 * @param board
	 * @calculates all the possible moves for the piece 
	 */
	public Collection<Move> calculateLegalMoves(final Board board) {
		
		final List<Move> legalMoves = new ArrayList<>();
		
		for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE) {
			final int candidateDestinationCoordinate = this.piecePosition+ currentCandidateOffset;
			
			if(isFirstCulumnExclusion(this.piecePosition, currentCandidateOffset)||
			   isFifthCulumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
				
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				final Tile currentTile = board.getTile(this.piecePosition);
				if(!candidateDestinationTile.isTileOccupied()) {
					if(candidateDestinationTile.getLayer()<4 &&(candidateDestinationTile.getLayer()<currentTile.getLayer() ||
																candidateDestinationTile.getLayer()==currentTile.getLayer()||
																candidateDestinationTile.getLayer()==currentTile.getLayer()+1)||
							(candidateDestinationTile.getLayer()==3 &&currentTile.getLayer() ==2)
					){

						
						legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
					}
				}else {
					System.out.println("------------------------------------\n OCCUPID TILE: "+candidateDestinationTile.getTileCordinate()+"-----"+candidateDestinationTile.getPiece().getPieceAlliance()+"\n====================");
				}
			}//-isValid
		}//!- For
		
		return legalMoves;
	}//!- calculateLegalMoves
	
	
	/**
	 * @param board
	 * @calculates all the possible build spots for the piece 
	 */
	public Collection<Move> calculateLegalBuildSpots(final Board board) {
		final List<Move> legalBuildSpots = new ArrayList<>();
		for(final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATE) {
			final int candidateDestinationCoordinate = this.piecePosition+ currentCandidateOffset;
			
			if(isFirstCulumnExclusion(this.piecePosition, currentCandidateOffset)||
				isFifthCulumnExclusion(this.piecePosition, currentCandidateOffset)) {
				continue;
			}
			if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
				final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
				final Tile currentTile = board.getTile(this.piecePosition);
				if(!candidateDestinationTile.isTileOccupied()) {
					if(candidateDestinationTile.getLayer()<4) {
						legalBuildSpots.add(new Move.BuildMove(board, this, candidateDestinationCoordinate));
					}
				}
			}//-isValid
		}//!- For
		return legalBuildSpots;
	}//!- calculateLegalBuildSpots
	
	
	/**
	 * @param currentPosition
	 * @param candidateOffset
	 * @return boolean: if the current position falls under the invalid locations
	 */
	private static boolean isFifthCulumnExclusion(int currentPosition, int candidateOffset) {
		return BoardUtils.FIFTH_COLUMN[currentPosition] && (candidateOffset ==-4||
															candidateOffset ==1||
															candidateOffset ==6);
	}
	private boolean isFirstCulumnExclusion(int currentPosition, int candidateOffset) {		
		return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset ==4||
															candidateOffset == -1||
															candidateOffset ==-6);
	}

	
	
		
	@Override
	public String toString() {
		if(this.pieceAlliance == Alliance.BLACK) {
			return "P";
		}else {
			return "C";
		}
	}
	
	public Piece movePiece(final Move move) {
		return new Piece( move.getDestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
	}
	
	
}
