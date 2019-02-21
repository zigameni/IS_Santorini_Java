package etf.santorini.sg160664d.board;

import java.util.*;

import etf.santorini.sg160664d.board.Move.LegalMoves;
import etf.santorini.sg160664d.gui.Table;
import etf.santorini.sg160664d.pieces.Piece;
import etf.santorini.sg160664d.player.BlackPlayer;
import etf.santorini.sg160664d.player.Player;
import etf.santorini.sg160664d.player.WhitePlayer;

/**
 * @author Gazmend Shehu
 */
public class Board {
    private static boolean firstTime = true;
	private List<Tile> gameBoard;
	private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private static Table.PlayerType whitePlayerType = Table.PlayerType.HUMAN;
    private static Table.PlayerType blackPlayerType = Table.PlayerType.HUMAN;
	private WhitePlayer whitePlayer;
	private BlackPlayer blackPlayer;
	private Player currentPlayer;

	private static boolean firstAccess =  true;

	/**
	 * The Board class is built using the Builder Design Pattern.
	 * @param builder
	 */
	private Board(Builder builder) {
		this.gameBoard = createGameBoard(builder);
		this.whitePieces = activePieces(this.gameBoard, Alliance.WHITE);
		this.blackPieces = activePieces(this.gameBoard, Alliance.BLACK);
		if(this.whitePieces.size()+ this.blackPieces.size() <4)firstAccess = true;
        else firstAccess = false;

		if(!(firstAccess)){
            LegalMoves whiteLegalMoves =  new LegalMoves(calculateLegalMoves(this.whitePieces),
                    calculateBuildMoves(this.whitePieces));
            LegalMoves blackLegalMoves =  new LegalMoves(calculateLegalMoves(this.blackPieces),
                    calculateBuildMoves(this.blackPieces));
            this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
            this.blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
            this.whitePlayer.setPlayerType(whitePlayerType);
            this.blackPlayer.setPlayerType(blackPlayerType);

            if(firstTime){
                this.currentPlayer = this.whitePlayer;
                firstTime = false;
            } else {
                this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
            }

        }
	}//!- Constructor


	/**
	 * This is the Copy Constructor
	 * @param board
	 */
	public Board(Board board){
	    this.gameBoard = cloneGameBoard(board.gameBoard);
	    this.whitePieces = cloneActivePieces(board.whitePieces);
        this.blackPieces = cloneActivePieces(board.blackPieces);

        LegalMoves whiteLegalMoves =  new LegalMoves(calculateLegalMoves(this.whitePieces),
                calculateBuildMoves(this.whitePieces));
        LegalMoves blackLegalMoves =  new LegalMoves(calculateLegalMoves(this.blackPieces),
                calculateBuildMoves(this.blackPieces));

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, whiteLegalMoves, blackLegalMoves);
        this.whitePlayer.setPlayerType(whitePlayerType);
        this.blackPlayer.setPlayerType(blackPlayerType);
        this.currentPlayer  = (board.currentPlayer == board.whitePlayer)?this.whitePlayer: this.blackPlayer;
    }//Copy Constructor



	/*
	Copy constructor helper method,
	clones the game board Tiles.
	 */
	private static List<Tile> cloneGameBoard(List<Tile> gameBoard) {
        List<Tile> clonedList = new ArrayList<>(gameBoard.size());
        for(Tile tile: gameBoard){
            if(tile.isTileOccupied()){
                clonedList.add(new Tile.OccupiedTile(tile.getTileCordinate(), new Piece(tile.getPiece()), tile.getLayer()));
            }else {
                clonedList.add(new Tile.EmptyTile(tile.getTileCordinate(), tile.getLayer()));
            }
        }
        return clonedList;
    }

    /*
    Helper method of the copy constructor, Clones the Active pieces
     */
    private static Collection<Piece> cloneActivePieces(Collection<Piece> ActivePieces){
        List<Piece> clonedCollection = new ArrayList<Piece>(ActivePieces.size());
        for (Piece piece: ActivePieces){
            clonedCollection.add(new Piece(piece));
        }
        return clonedCollection;
    }




	//getters
	public Collection<Piece> getWhitePieces(){return this.whitePieces;}
	public Collection<Piece> getBlackPieces(){return this.blackPieces;}
	public Player getBlackPlayer() {return this.blackPlayer;}
	public Player getWhitePlayer() {return this.whitePlayer;}
	public Player getCurrentPlayer() {return this.currentPlayer;}
	public List<Tile> getGameBoard(){return this.gameBoard;}
	public boolean isFirstAccess(){return this.firstAccess;}


	//calculating all the legal Moves for the given collection of pieces
	private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final Piece piece: pieces) {
			legalMoves.addAll(piece.calculateLegalMoves(this));
		}
		return legalMoves;
	}//!- calculateLegalMoves

	/**
	 * Here we set the kind of player playes with the white pieces: Human or Computer
	 * @param playerType
	 */
	public void setWhitePlayerType(Table.PlayerType playerType){Board.whitePlayerType= playerType; /*this.whitePlayer.setPlayerType(playerType);*/}
	/**
	 * Here we set the kind of player playes with the black pieces: Human or Computer
	 * @param playerType
	 */
	public void setBlackPlayerType(Table.PlayerType playerType){Board.blackPlayerType= playerType; /*this.blackPlayer.setPlayerType(playerType);*/}

	/**
	 * This method returns the player type that plays with the black pieces.
	 * @return black PLayer Type
	 */
	public static  Table.PlayerType getBlackPlayerType() {
		return blackPlayerType;
	}

	/**
	 * This method returns the player type that plays with the White pieces.
	 * @return White PLayer Type
	 */
	public  static Table.PlayerType getWhitePlayerType(){
		return whitePlayerType;
	}

	/**
	 * This method resets all the layers  of every tile on the board.
	 */
	public void resetTileLayers(){
		for(Tile tile: gameBoard){
			tile.setLayer(0);
		}
		firstTime = true;
	}


	/**
	 * Calculates all the possible build spots for a collection of pieces.
	 * @param pieces
	 * @return collection of legal build moves
	 */
	private Collection<Move> calculateBuildMoves(final Collection<Piece> pieces) {
		final List<Move> legalMoves = new ArrayList<>();
		for(final Piece piece: pieces) {
			legalMoves.addAll(piece.calculateLegalBuildSpots(this));
		}
		return legalMoves;
	}//!- calculateBuildMoves

	/**
	 * Method to get all of the active pieces of a board based on the alliance
	 * @param gameBoard
	 * @param alliance
	 * @return collection of active pieces of the given alliance
	 */
	private Collection<Piece> activePieces(final List<Tile> gameBoard,
										   final  Alliance alliance) {
	    final List<Piece> activePieces = new ArrayList<>();
		for(final Tile tile: gameBoard) {
			if(tile.isTileOccupied()) {
				final Piece piece = tile.getPiece();
				if(piece.getPieceAlliance() == alliance) {
					activePieces.add(piece);
				}
			}
		}
		//System.out.println("ACTIVE PIECES SIZE: "+ activePieces.size());
		return activePieces;
	}//!- activePieces

	/**
	 * Return the tile based on the id.
	 * @param tileCoordinate
	 * @return the board tile with the given id.
	 */
    public Tile getTile(final int tileCoordinate) {
		return gameBoard.get(tileCoordinate );
	}
	
	/*
	Using the builder this is where we create the game board tiles.
	 */
	private static List<Tile> createGameBoard(final Builder builder){
		final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
		
		//first making
		for(int i =0; i<BoardUtils.NUM_TILES; i++) {
			tiles[i]= Tile.createTile(i, builder.boardConfig.get(i), 0);
		}
		return Arrays.asList(tiles);
	}

	/**
	 * The standard board is created after the players have inserted their pieces.
	 * @return board
	 */
	public static Board createStandardBoard() {
		final Builder builder = new Builder();
		builder.setMoveMaker(Alliance.WHITE);
		return builder.build();
	}

	/**
	 * This method is ued to insert a certain piece into the game based on the coordinate and the alliance.
	 * @param board
	 * @param coordinate
	 * @param alliance
	 * @return the board after the piece is inserted
	 */
	public static Board createBoardInsertPiece(final Board board, final int coordinate, final Alliance alliance){
	    final Builder builder = new Builder();
	    for(Tile tile : board.gameBoard){
	        if(tile.getPiece()!= null){
	            builder.setPiece(new Piece(tile.getTileCordinate(), tile.getPiece().getPieceAlliance()));
            }
        }
	    builder.setPiece(new Piece(coordinate, alliance));
        return builder.build();
    }//!-  Create standrad Board
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i =0; i< BoardUtils.NUM_TILES; i++) {
			sb.append(gameBoard.get(i).toString());
			sb.append(" ");
			if((i+1)%BoardUtils.NUM_TILES_PER_ROW ==0) {
				sb.append("\n");
			}			
		}
		return sb.toString();
	}

	/**
	 * This method checks if the game has come to a end scenario.
	 * @return a true or false value
	 */
	public boolean isEndGame(){
		if(checkWinner() == null){
			return false;
		}else {
			return true;
		}
	}

	/**
	 * This method checks which player has won if there is a winner.
	 * @return The player that has won, or null if the game has not ended.
	 */
	public Player checkWinner(){
	    if(checkIfBlackPlayerWon()){
            return this.blackPlayer;
        }else if(checkIfWhitePlayerWon()){
	        return this.whitePlayer;
        }else {
	        return null;
        }
    }//!- checkWinner

   //Checks if the black player has won
    private boolean checkIfBlackPlayerWon() {
        Collection<Move>  legalWhite = calculateLegalMoves(this.whitePieces);
        if(legalWhite.isEmpty()){
            return true;
        }
        for(Piece piece: this.blackPieces){
            if(gameBoard.get(piece.getPiecePosition()).getLayer()==3 ){
                return true;
            }
        }
        return false;
    }//!- checkIfBlackPLayerWon

    //CHECK IF WHITE PLAYER HAS WON
    private boolean checkIfWhitePlayerWon() {
        Collection<Move>  legalBlack = calculateLegalMoves(this.blackPieces);
        if(legalBlack.isEmpty()){
            return true;
        }
        for(Piece piece: this.whitePieces){
            if(gameBoard.get(piece.getPiecePosition()).getLayer()==3 ){
                return true;
            }
        }
        return false;
    }//!- checkIfWhitePLayerWon

	/**
	 * This method calculates the number of active pieces on the board.
	 * @return number of active pieces.
	 */
	public int numberOfActivePieces() {
		return this.blackPieces.size() + this.whitePieces.size();
	}

	/**
	 * Builder Class for the Board class.
	 */
	public static class Builder {
		Map<Integer, Piece> boardConfig;
		Alliance nextMoveMaker;

		/**
		 * Constructor creates the hash map used for the board Configuration
		 */
		public Builder() {
			boardConfig = new HashMap<>();
			nextMoveMaker = Alliance.WHITE;
		}

		/**
		 * This method is used to set a certain piece on its position.
		 * @param piece
		 * @return Builder
		 */
		public Builder setPiece(final Piece piece) {
			this.boardConfig.put(piece.getPiecePosition(), piece);
			return this;
		}
		
		/**
		 *  Here we set the next player that is going to move.
		 * @param nextMoveMaker
		 * @return Builder
		 */
		public Builder setMoveMaker(final Alliance nextMoveMaker) {
			this.nextMoveMaker = nextMoveMaker;
			return this;
		}

		/**
		 * This method creates a board based on the builder specifications
		 * @return Board
		 */
		public Board build() {
			return new Board(this);
		}
	}//!- Builder
}//!- Board
