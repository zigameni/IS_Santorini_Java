package etf.santorini.sg160664d.player.ai;

import etf.santorini.sg160664d.board.Board;

public interface BoardEvaluator {
    int evaluate(Board board, int depth);
}
