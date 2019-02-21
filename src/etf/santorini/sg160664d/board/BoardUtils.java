package etf.santorini.sg160664d.board;

/**
 * @author Gazmend Shehu
 */
public class BoardUtils {
	public static final int NUM_TILES = 25;
	public static final int NUM_TILES_PER_ROW = 5;
	
	public static final boolean[] FIRST_COLUMN = initColumn(0);
	public static final boolean[] FIFTH_COLUMN = initColumn(4);
	//public static final boolean[] SECOND_COLUMN = initColumn(1);
	//public static final boolean[] FOURTH_COLUMN = initColumn(3);
	
	//Constructor
	private BoardUtils() {
		throw new RuntimeException("You stupid. You cannot instantiate me!");
	}
	
	//initializes the specific coordinates on the column with true
	private static boolean[] initColumn(int columnNumber) {
		final boolean[]column = new boolean[NUM_TILES];
		do {
			column[columnNumber] = true;
			columnNumber+=NUM_TILES_PER_ROW;
		}while(columnNumber<NUM_TILES);
		return column;
	}
	
	//check if the coordinate is inside the bounds
	public static boolean isValidTileCoordinate(int coordinate) {
		return coordinate >= 0 && coordinate <NUM_TILES;
	}
}
