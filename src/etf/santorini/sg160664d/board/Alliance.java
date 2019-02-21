package etf.santorini.sg160664d.board;

import etf.santorini.sg160664d.player.BlackPlayer;
import etf.santorini.sg160664d.player.Player;
import etf.santorini.sg160664d.player.WhitePlayer;

/**
 * @Author Gazmend Shehu 
 */
public enum Alliance {
	WHITE {
		@Override
		public boolean isBlack() {return false;}

		@Override
		public boolean isWhite() {return true;}

		@Override
		protected Player choosePlayer(WhitePlayer whitePlayer, 
										BlackPlayer blackPlayer) {
			return whitePlayer;
		}
	},
	BLACK {
		@Override
		public boolean isBlack() {return true;}

		@Override
		public boolean isWhite() {return false;}

		@Override
		protected Player choosePlayer(WhitePlayer whitePlayer, 
										BlackPlayer blackPlayer) {
			return blackPlayer;
		}
	};
	
	public abstract boolean isBlack();
	public abstract boolean isWhite();
	protected abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}//!- Alliance
