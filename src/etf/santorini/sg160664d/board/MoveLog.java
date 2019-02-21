package etf.santorini.sg160664d.board;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import etf.santorini.sg160664d.board.Board.Builder;
import etf.santorini.sg160664d.board.Move.MoveType;
import etf.santorini.sg160664d.gui.Table;
import etf.santorini.sg160664d.pieces.Piece;
import etf.santorini.sg160664d.player.MoveTransition;

/**
 * @author Gazmend Shehu
 */
public class MoveLog{
	private Scanner x;
    private final List<Move> moves;
	private int index;
    /**
     * Constructor of the Move Log,
     * The Move Log keeps a log of all the played Moves.
     */
    public MoveLog(){
        this.moves = new ArrayList<>();
        //this.untestedMoves = new ArrayList();
    }

    /**
     * Copy Constructor
     * @param moveLog
     */
    public MoveLog(final MoveLog moveLog){
    	this.moves = new ArrayList<>();
    	for(Move move: moveLog.moves){
    		this.addMove(move);
		}
	}

    /**
     * This method executes all the moves of the move log.
     * @return board
     */
    public Board executeMoveLog(){
		Builder builder = new Builder();
		Board board=null;
		Piece piece1=null, piece2=null, piece3=null, piece4=null;
		int counter =0;
    	for(Move move: moves){
    		if(counter< 4) {
				if(move.getMoveType()==MoveType.INSERT_Move){
					switch (counter) {
						case 0:
							piece1 = new Piece(move.getSourceCoordinate(), Alliance.WHITE);
							counter ++;
							break;
						case 1:
							piece2 = new Piece(move.getSourceCoordinate(), Alliance.WHITE);
							counter ++;
							break;

						case 2:
							piece3 = new Piece(move.getSourceCoordinate(), Alliance.BLACK);
							counter ++;
							break;
						case 3:
							piece4 = new Piece(move.getSourceCoordinate(), Alliance.BLACK);

							builder.setPiece(piece1);
							builder.setPiece(piece2);
							builder.setPiece(piece3);
							builder.setPiece(piece4);
							builder.setMoveMaker(Alliance.WHITE);
							board = builder.build();

							board.setBlackPlayerType(Table.PlayerType.HUMAN);
							counter ++;
							break;
						}
				}
		}else {
				if(move.getMoveType() == MoveType.MAJOR_Move){
					MoveTransition transition;
					final Move majorMove = new Move.MajorMove(board, board.getTile(move.getSourceCoordinate()).getPiece(), move.getDestinationCoordinate());
					transition = board.getCurrentPlayer().makePieceMove(majorMove);
					if(transition.getMoveStatus().isDone()){
						board = transition.getTransitionBoard();
					}
				}else if(move.getMoveType() == MoveType.BUILD_Move){
					MoveTransition transition;
					final Move buildMove = new Move.BuildMove(board, board.getTile(move.getSourceCoordinate()).getPiece(), move.getDestinationCoordinate());
					transition = board.getCurrentPlayer().makeBuildMove(buildMove);
					if(transition.getMoveStatus().isDone()) {
						board = transition.getTransitionBoard();
					}
				}
			}
		}
    	return board;
	}

    /**
     *
     * @return list of played moves
     */
    public List<Move> getMoves() {return this.moves; }

    /**
     * Using this method we can add a move to the move log.
     * @param move
     */
    public void addMove(final Move move){ this.moves.add(move); }

    /**
     * Using this method we can get the size of the move log.
     * @return number of moves played.
     */
    public int size(){ return this.moves.size(); }

    /**
     * Using this method we can clear the move log.
     */
    public void clear(){ this.moves.clear();}

    /**
     * Using this method we can get && remove a certain move based on index.
     * @param index
     * @return Move
     */
    public Move removeMove(final int index){
        return this.moves.remove(index);
    }
	public Move getNextMove(){
    	return this.moves.get(index++);
    }

	public void setIndex(){this.index =0;}
    public boolean removeMove(final Move move){
        return this.moves.remove(move);
    }

    /**
     * Using this method we can read s sequence of moves form a file.
     * @param file
     * @return board
     */
    public Board readMovesFormFile(final File file) {
    	clear();
    	openFile(file);
   
    	Builder builder = new Builder();
    	Board board=null;
    	
    	int counter = 0, elemCounter =0;
    	String tempStr;
    	int sourceCoordinate=0, destinationCoordinate=0, buildCoordinate=0;
    	Piece piece1=null, piece2=null, piece3=null, piece4=null;
    	while(x.hasNext()) {
    		if(counter <4) {
    			switch (counter) {
    			case 0:
    				tempStr = x.next();
    				piece1 = new Piece(Move.getIntegerCordinate(tempStr), Alliance.WHITE);
    				break;
    			case 1:
    				tempStr= x.next();
    				piece2 = new Piece(Move.getIntegerCordinate(tempStr), Alliance.WHITE);
    				x.nextLine();
					break;
    			case 2:	
    				tempStr= x.next();
    				piece3 = new Piece(Move.getIntegerCordinate(tempStr), Alliance.BLACK);
					break;
    			case 3:
    				tempStr= x.next();
    				x.nextLine();
    				//add all moves here.
    				piece4 = new Piece(Move.getIntegerCordinate(tempStr), Alliance.BLACK);
    				builder.setPiece(piece1);
    				builder.setPiece(piece2);
    				builder.setPiece(piece3);
    				builder.setPiece(piece4);
    				builder.setMoveMaker(Alliance.WHITE);
    				board = builder.build();
    				moves.add(new Move.InsertPieceMove(board, piece1, piece1.getPiecePosition()));
    				moves.add(new Move.InsertPieceMove(board, piece2, piece2.getPiecePosition()));
    				moves.add(new Move.InsertPieceMove(board, piece3, piece3.getPiecePosition()));
    				moves.add(new Move.InsertPieceMove(board, piece4, piece4.getPiecePosition()));
    				board.setBlackPlayerType(Table.PlayerType.HUMAN);
    				board.setWhitePlayerType(Table.PlayerType.HUMAN);
    				break;
				}
    			counter++;
    		}else {
    			if(elemCounter==0) {
    				tempStr = x.next();
    				sourceCoordinate = Move.getIntegerCordinate(tempStr);
    				elemCounter++;
    			}else if(elemCounter ==1) {
    				tempStr = x.next();
    				destinationCoordinate = Move.getIntegerCordinate(tempStr);
    				elemCounter++;
    			}else if(elemCounter ==2) {
    				tempStr = x.next();
    				buildCoordinate = Move.getIntegerCordinate(tempStr);
    				elemCounter =0;
    				x.nextLine();
    				MoveTransition transition;
    				final Move majorMove = new Move.MajorMove(board, board.getTile(sourceCoordinate).getPiece(), destinationCoordinate);
    				transition = board.getCurrentPlayer().makePieceMove(majorMove);
    				if(transition.getMoveStatus().isDone()) {
    					board = transition.getTransitionBoard();
    					moves.add(majorMove);
    					final Move buildMove = new Move.BuildMove(board, board.getTile(majorMove.getDestinationCoordinate()).getPiece(), buildCoordinate);
    					transition = board.getCurrentPlayer().makeBuildMove(buildMove);
    					if(transition.getMoveStatus().isDone()) {
    						board = transition.getTransitionBoard();
    						moves.add(buildMove);
    					}
    				}
    			}
    		}
    	}
    	x.close();
    	return board;
    	
    }//!- readMovesFromFile

    private void openFile(File file) {
		try {
			x = new Scanner(file);
		}catch(Exception e) {
			System.out.println("ERROR: No file found.");
		}
	}

    /**
     * We use this method to print the move log to a file.
     * @return string.
     */
	@Override
    public String toString() {
        int index =0;
        boolean insertPieces = true;
        int brojac =0; // do treceg poteza su Insert_Move
        Move previousMajorMove = null;
        
        //return moves.toString();
        StringBuilder stringBuilder = new StringBuilder();
        for(Move move: moves){
        	if(move.getMoveType() == MoveType.INSERT_Move) {
        		if(brojac<2) {
        			if(brojac==0) {
            			stringBuilder.append(Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
            			brojac++;
            		}else if(brojac ==1) {
            			stringBuilder.append(Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
            			stringBuilder.append("//postavlaju se figure prvog igraca na oznacena pola\n");
            			brojac++;
            		}
        		}else {
        			if(brojac == 2) {
        				stringBuilder.append(Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
        				brojac++;
        			}else if(brojac ==3) {
            			stringBuilder.append(Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
            			stringBuilder.append("//postavlaju se figure drugog igraca na oznacena pola\n");
            			brojac++;
            			insertPieces= false;
            		}
        		}
        	//!- INSERT_Move
        	}else if(move.getMoveType() == MoveType.MAJOR_Move) {
        		stringBuilder.append(Move.getStringCoordinate(move.getSourceCoordinate())+" "+
        							 Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
        		previousMajorMove = move;
        	//!- MAJOR_Move	
        	}else if(move.getMoveType() == MoveType.BUILD_Move && previousMajorMove!= null){
        		stringBuilder.append(Move.getStringCoordinate(move.getDestinationCoordinate())+" ");
        		stringBuilder.append("//pomera se figura sa pola "+
        								Move.getStringCoordinate(previousMajorMove.getSourceCoordinate())+
        								" na pole "+	
        								Move.getStringCoordinate(previousMajorMove.getDestinationCoordinate())+
        								", uz izgradnju plocice na polu "+
        								Move.getStringCoordinate(move.getDestinationCoordinate())+ "\n"	);
        	//!- BUILD_Move
        	}
        }
        System.out.println("THIS IS THE MOVE LOG: \n"+ stringBuilder.toString());
        return stringBuilder.toString();
    }//!- To String
}//!- MoveLog
