package etf.santorini.sg160664d.player.ai;

import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Tile;
import etf.santorini.sg160664d.pieces.Piece;
import etf.santorini.sg160664d.player.Player;

public class StandardBoardEvaluator implements BoardEvaluator {
    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.getWhitePlayer(), depth)-
                scorePlayer(board, board.getBlackPlayer(), depth);
    }

    private int scorePlayer(final Board board,
                            final Player player,
                            final int depth) {
        return pieceValue(player, board)+
                mobility(player, board);
        //Later we can add other criteria
        // + layersAround  + opponentBlocked...

    }

    /**
     * Calculates how many legal moves the player has;
     * @param player
     * @param board
     * @return nrOfLegalMoves
     */
    private int mobility(final Player player, final Board board) {
        return player.getLegalMoves().getStandardLegalMoves().size();
    }

    /**
     * Here we calculate the value of the board based on the layers the pieces are on;
     * @param player
     * @param board
     * @return value
     */
    private int pieceValue(final Player player, final Board board) {
        int value = 0;
        for(Piece piece: player.getActivePieces()){
            Tile tile = board.getTile(piece.getPiecePosition());
            switch (tile.getLayer()){
                case 0:  value +=1;
                    break;
                case 1: value +=10;
                    break;
                case 2: value +=20;
                    break;
                case 3: value +=100;
                    break;
            }
        }
        return value;
    }//pieceValue

}//!- StandardBoardEvaluator
