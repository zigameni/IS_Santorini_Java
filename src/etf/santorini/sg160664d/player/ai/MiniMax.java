package etf.santorini.sg160664d.player.ai;

import etf.santorini.sg160664d.board.*;
import etf.santorini.sg160664d.pieces.Piece;
import etf.santorini.sg160664d.player.MoveTransition;

import java.util.Collection;

public class MiniMax {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private Move majorMove= null;
    private Move buildMove= null;

    private Move bestMajorMove = null;
    private Move bestBuildMove = null;

    private int najBoljaVrednost;

    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    public void execute(final Board board, final MoveLog moveLog){
        int bestValue;
        int currentValue;
        if(board.getCurrentPlayer().getAlliance().isWhite()){
            bestValue = Integer.MIN_VALUE;//white player = Max
        }else {
            bestValue = Integer.MAX_VALUE;//black player = MIN
        }

        Alliance alliance = board.getCurrentPlayer().getAlliance();
        for(final Move majorMove1: board.getCurrentPlayer().getLegalMoves().getStandardLegalMoves()) {
            final Board majorTempBoard = moveLog.executeMoveLog();
            MoveLog moveLog1 = new MoveLog(moveLog);
            Move tempMove = new Move.MajorMove(majorTempBoard,
                                                majorTempBoard.getTile(majorMove1.getSourceCoordinate()).getPiece(),
                                                majorMove1.getDestinationCoordinate());
            final MoveTransition majorMoveTransition = majorTempBoard.getCurrentPlayer().makePieceMove(tempMove);
            if (majorMoveTransition.getMoveStatus().isDone()) {
                moveLog1.addMove(tempMove);

                final Piece movedPiece = majorMoveTransition.getTransitionBoard().getTile(majorMove1.getDestinationCoordinate()).getPiece();

                  for (final Move buildMove1 : movedPiece.calculateLegalBuildSpots(majorMoveTransition.getTransitionBoard())){
                      Move tempBuildMove =new Move.BuildMove(majorMoveTransition.getTransitionBoard(),
                                                              majorMoveTransition.getTransitionBoard().getTile(buildMove1.getSourceCoordinate()).getPiece(),
                                                              buildMove1.getDestinationCoordinate());
                      final MoveTransition buildMoveTransition = majorMoveTransition.getTransitionBoard().getCurrentPlayer().makeBuildMove(tempBuildMove);

                    if (buildMoveTransition.getMoveStatus().isDone()) {
                        MoveLog moveLog2 = new MoveLog(moveLog1);
                        moveLog2.addMove(tempBuildMove);
                        final Board newStateBoard = buildMoveTransition.getTransitionBoard();
                        currentValue = miniMaxExecute(newStateBoard, searchDepth, moveLog2, 1);
                        if (alliance == Alliance.WHITE && currentValue > bestValue) {
                            bestValue= currentValue;
                            bestMajorMove = new Move.MajorMove(board,
                                    new Piece(majorMove1.getSourceCoordinate(), majorMove1.getMovedPiece().getPieceAlliance()),
                                    majorMove1.getDestinationCoordinate()); // majorMove1;
                            bestBuildMove = new Move.BuildMove(majorMoveTransition.getTransitionBoard(),
                                    new Piece(buildMove1.getSourceCoordinate(), buildMove1.getMovedPiece().getPieceAlliance()),
                                    buildMove1.getDestinationCoordinate());
                        } else if (alliance == Alliance.BLACK && currentValue < bestValue) {
                            bestValue = currentValue;
                            bestMajorMove = new Move.MajorMove(board,
                                    new Piece(majorMove1.getSourceCoordinate(), majorMove1.getMovedPiece().getPieceAlliance()),
                                    majorMove1.getDestinationCoordinate()); // majorMove1;
                            bestBuildMove = new Move.BuildMove(majorMoveTransition.getTransitionBoard(),
                                    new Piece(buildMove1.getSourceCoordinate(), buildMove1.getMovedPiece().getPieceAlliance()),
                                    buildMove1.getDestinationCoordinate());
                        }
                    }
                }
            }
        }
        this.majorMove = bestMajorMove;
        this.buildMove = bestBuildMove;
    }//!- execute

    private int miniMaxExecute(final Board board, int maxDepth, MoveLog moveLog, int currentDepth){
    //Ako je terminalno stanje ili trenutna dubina jednaka maksimalnoj
        if(maxDepth == currentDepth|| isEndGameScenario(board)){
        return this.boardEvaluator.evaluate(board, maxDepth);
    }
    int bestValue;
    int currentValue;

        if(board.getCurrentPlayer().getAlliance().isWhite()){
        bestValue = Integer.MIN_VALUE;//white player = Max
    }else {
        bestValue = Integer.MAX_VALUE;//black player = MIN
    }

    Alliance alliance = board.getCurrentPlayer().getAlliance();
        for(final Move majorMove1: board.getCurrentPlayer().getLegalMoves().getStandardLegalMoves()) {
        final Board majorTempBoard = moveLog.executeMoveLog();
        MoveLog moveLog1 = new MoveLog(moveLog);
        Move tempMove = new Move.MajorMove(majorTempBoard,
                majorTempBoard.getTile(majorMove1.getSourceCoordinate()).getPiece(),
                majorMove1.getDestinationCoordinate());
        final MoveTransition majorMoveTransition = majorTempBoard.getCurrentPlayer().makePieceMove(tempMove);
        if (majorMoveTransition.getMoveStatus().isDone()) {
            moveLog1.addMove(tempMove);

            final Piece movedPiece = majorMoveTransition.getTransitionBoard().getTile(majorMove1.getDestinationCoordinate()).getPiece();

            for (final Move buildMove1 : movedPiece.calculateLegalBuildSpots(majorMoveTransition.getTransitionBoard())){
                Move tempBuildMove =new Move.BuildMove(majorMoveTransition.getTransitionBoard(),
                        majorMoveTransition.getTransitionBoard().getTile(buildMove1.getSourceCoordinate()).getPiece(),
                        buildMove1.getDestinationCoordinate());
                final MoveTransition buildMoveTransition = majorMoveTransition.getTransitionBoard().getCurrentPlayer().makeBuildMove(tempBuildMove);
                if (buildMoveTransition.getMoveStatus().isDone()) {
                    MoveLog moveLog2 = new MoveLog(moveLog1);
                    moveLog2.addMove(tempBuildMove);
                    final Board newStateBoard = buildMoveTransition.getTransitionBoard();
                    currentValue = miniMaxExecute(newStateBoard, searchDepth, moveLog2, currentDepth + 1);
                    if (alliance == Alliance.WHITE && currentValue > bestValue) {
                        bestValue= currentValue;
                    } else if (alliance == Alliance.BLACK && currentValue < bestValue) {
                        bestValue = currentValue;
                    }
                }
            }
        }
    }
        return bestValue;
}//!- Execute

    public Move getMajorMove(){return this.majorMove;}
    public Move getBuildMove(){return this.buildMove;}

    private boolean isEndGameScenario(Board board) {
        return board.isEndGame();
    }
}//- MINI_MAX
