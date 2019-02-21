package etf.santorini.sg160664d.board;

import java.util.Collection;

import etf.santorini.sg160664d.board.Board.Builder;
import etf.santorini.sg160664d.pieces.Piece;

/**
 * @author Gazmend Shehu
 * This is the main Move class
 */
public abstract class Move {	
	final Board board;
	final Piece movedPiece;
	final int destinationCoordinate;


    /**
     * Constructor of Move
     * @param board
     * @param movedPiece
     * @param destinationCoordinate
     */
	public  Move(final Board board, 
					final Piece movedPiece, 
					final int destinationCoordinate) {
		this.board = board;
		this.movedPiece = movedPiece;
		this.destinationCoordinate = destinationCoordinate;
	}

    /**
     * @return Destination Coordinate
     */
	public int getDestinationCoordinate() {return this.destinationCoordinate;}

    /**
     * This is where we get the moved Piece
     * @return Moved Piece
     */
	public Piece getMovedPiece() {return this.movedPiece;}

    /**
     * This is where we get the source coordinate of the moved piece.
     * @return source Coordinate
     */
	public int getSourceCoordinate() {return movedPiece.getPiecePosition();}
	public abstract Board execute();
	public abstract MoveType getMoveType();

    /**
     * This method converts a given coordinate to its equivalent notation.
     * @param coordinate
     * @return string notation
     */
	public static String getStringCoordinate(int coordinate){
		
		String [] coordinateFormat = {"A5", "B5", "C5", "D5", "E5",
									  "A4", "B4", "C4", "D4", "E4",
									  "A3", "B3", "C3", "D3", "E3",
									  "A2", "B2", "C2", "D2", "E2",
									  "A1", "B1", "C1", "D1", "E1",};
		if(coordinate <=25 && coordinate >=0)
			return coordinateFormat[coordinate];
		else return "00";
	}

    /**
     * This method converts the string notation to its equivalent integer coordinate.
     * @param stringNotation
     * @return the integer coordinate
     */
	public static int getIntegerCordinate(final String stringNotation) {
		String [] coordinateFormat = {"A5", "B5", "C5", "D5", "E5",
									  "A4", "B4", "C4", "D4", "E4",
									  "A3", "B3", "C3", "D3", "E3",
									  "A2", "B2", "C2", "D2", "E2",
									  "A1", "B1", "C1", "D1", "E1",};
		for(int i = 0; i<25; i++) {
			if( coordinateFormat[i].equals(stringNotation) ) {
				return i;
			}
		}
		return -1;
	}

    /**
     * Move Type
     */
    public enum MoveType{
        MAJOR_Move,
        BUILD_Move,
        INSERT_Move
    }

	public static class BuildMove extends Move {
		public BuildMove(Board board, Piece piece, int destinationCoordinate) {
			super(board, piece, destinationCoordinate);
		}

        /**
         * This method does the execution of the build Move.
         * @return Board after the move has been executed.
         */
		@Override
		public Board execute() {
			final Builder builder = new Builder();
			//set pieces of the current player
			for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()) {
				builder.setPiece(new Piece(piece.getPiecePosition(), piece.getPieceAlliance()));
			}
			//set opponents pieces
			for(final Piece piece: this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(new Piece(piece.getPiecePosition(), piece.getPieceAlliance()));
			}
			
			builder.setMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
			Board newBoard = builder.build();			
			
			//copy tile layers
			for(int i =0; i < BoardUtils.NUM_TILES; i++) {
				int layer = board.getGameBoard().get(i).getLayer();
				newBoard.getGameBoard().get(i).setLayer(layer);
			}
			newBoard.getGameBoard().get(destinationCoordinate).incLayer();
			return newBoard;
		}//!- Execute

        @Override
		public String toString() {
			return (getStringCoordinate(destinationCoordinate)+" "+ destinationCoordinate);
		}

        /**
         * This method returns the type of the Move.
         * @return BUILD_MOVE
         */
		@Override
		public MoveType getMoveType() {
			return MoveType.BUILD_Move;
		}
	}//!-BuildMove
	
	/* =================* MAJOR_MOVE *============== */
	public static class MajorMove extends Move{
		public MajorMove(Board board, Piece piece, int candidateDestinationCoordinate) {
			super(board, piece, candidateDestinationCoordinate);
		}
        /**
         * This method does the execution of the major Move.
         * @return Board after the move has been executed.
         */
		@Override
		public Board execute() {
			final Builder builder = new Builder();

			for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()) {
				if(!this.movedPiece.equals(piece)) {
					builder.setPiece(new Piece(piece.getPiecePosition(), piece.getPieceAlliance()));
				}
			}
			for(final Piece piece: this.board.getCurrentPlayer().getOpponent().getActivePieces()) {
				builder.setPiece(new Piece(piece.getPiecePosition(), piece.getPieceAlliance()));
			}

			//move the moved piece
			Tile tile = board.getGameBoard().get(movedPiece.getPiecePosition());
            if(tile.isTileOccupied()){
                board.getGameBoard().set(tile.getTileCordinate(), new Tile.EmptyTile(tile.getTileCordinate(),tile.getLayer()));
            }

            builder.setPiece(this.movedPiece.movePiece(this));
			builder.setMoveMaker(this.board.getCurrentPlayer().getAlliance());
			Board newBoard = builder.build();
			//copy tile layers
			for(int i =0; i < BoardUtils.NUM_TILES; i++) {
				int layer = board.getGameBoard().get(i).getLayer();
				newBoard.getGameBoard().get(i).setLayer(layer);
			}
			return newBoard;
		}

		
		@Override
		public String toString() {
			return (getStringCoordinate(movedPiece.getPiecePosition())+" "+getStringCoordinate(destinationCoordinate)+" "+ movedPiece.getPiecePosition()+ destinationCoordinate);
		}

        /**
         * This method return the move type.
         * @return MAJOR_MOVE
         */
		@Override
		public MoveType getMoveType() {
			return MoveType.MAJOR_Move;
		}
	}//!-MajorMove


    public static class InsertPieceMove extends Move {
        //private MoveType moveType = MoveType.INSERT_Move;
        public InsertPieceMove(Board board, Piece piece, int candidateDestinationCoordinate) {
            super(board, piece, candidateDestinationCoordinate);
        }

        @Override
        public Board execute() {
            return null;
        }

        @Override
        public MoveType getMoveType() {return MoveType.INSERT_Move;}

        @Override
        public String toString() {
            return (getStringCoordinate(destinationCoordinate)+" "+ destinationCoordinate);
        }
    }

	/* ================* LEGAL_MOVES *============= */
	public static class LegalMoves {
		private Collection<Move> standardLegalMoves;
		private Collection<Move> buildLegalMoves;

        /**
         * Constructor of the Legal Moves Class
         * @param standardLegalMoves
         * @param buildLegalMoves
         */
		public LegalMoves(Collection<Move> standardLegalMoves, 
							Collection<Move> buildLegalMoves) {
			this.standardLegalMoves = standardLegalMoves;
			this.buildLegalMoves = buildLegalMoves;
		}

        /**
         * This method returns all the standard legal moves
         * @return standard Legal Moves collection
         */
		public Collection<Move> getStandardLegalMoves(){return this.standardLegalMoves;}

        /**
         * This method return all the legal Build Moves.
         * @return legal Build Moves collection
         */
		public Collection<Move> getBuildLegalMoves(){return this.buildLegalMoves;}

        /**
         * Here we check if the given move is a standard Legal Move.
         * @param move
         * @return true or false
         */
		public boolean isStandardLegalMove(final Move move) {
			for(final Move move1: standardLegalMoves){
				if(move.getMovedPiece().getPiecePosition() == move1.getMovedPiece().getPiecePosition()  &&
						move.getDestinationCoordinate() == move1.getDestinationCoordinate()){
					return true;
				}
			}
			return false;
		}

        /**
         * Here we check if the given move is a Legal build move.
         * @param move
         * @return
         */
		public boolean isLegalBuildMove(final Move move) {
			for(final Move move1: buildLegalMoves){
				if(move.getMovedPiece().getPiecePosition() == move1.getMovedPiece().getPiecePosition()  &&
						move.getDestinationCoordinate() == move1.getDestinationCoordinate()){
					return true;
				}
			}
			return false;
		}
	}//!-LegalMoves
}//!-Move
