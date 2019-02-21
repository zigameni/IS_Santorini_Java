package etf.santorini.sg160664d.player.ai;

import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.board.Move;

public interface MoveStrategy {
    Move execute(Board board);
}
