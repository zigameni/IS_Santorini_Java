package etf.santorini.sg160664d.player;

public enum MoveStatus {
	DONE {
		@Override
		public boolean isDone() {return true;}
	},
	ILEGAL_MOVE {
		@Override
		public boolean isDone() {return false;}
	};
	public abstract boolean isDone();
}
