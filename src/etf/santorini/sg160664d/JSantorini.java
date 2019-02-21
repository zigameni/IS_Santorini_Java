package etf.santorini.sg160664d;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import etf.santorini.sg160664d.board.Board;
import etf.santorini.sg160664d.gui.Table;

/**
 * 
 * @author Gazmend Sehu 0664_2016
 * Board Game: Santorini
 *
 */

/*=========== * MAIN * ==============   */
public class JSantorini {
	
	public static void main(String [] args) {
		
		
		 try {
	            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	        } catch (Exception e) { }
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	new Table();
	            }
	        });
		
		//Board board = Board.createStandardBoard();
		//System.out.println(board.toString());
	}
	
}
