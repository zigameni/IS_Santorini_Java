package etf.santorini.sg160664d.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import etf.santorini.sg160664d.board.*;
import etf.santorini.sg160664d.pieces.Piece;
import etf.santorini.sg160664d.player.MoveStatus;
import etf.santorini.sg160664d.player.MoveTransition;
import etf.santorini.sg160664d.player.Player;
import etf.santorini.sg160664d.player.ai.MiniMax;
import etf.santorini.sg160664d.player.ai.MoveStrategy;


/**
 *This is one of the most important classes of this project, it contains the frame whitch contains the whole user interfase.
 *
  */
public class Table extends Observable {
	//Gui components
	private final JFrame gameFrame;
	private PlayerToMove playerToMove;
	private BoardPanel boardPanel;
	//private Label statusBar;
	private JButton nextButton;
	private File mainFile;
	private MoveLog moveLog;
	private PlayerType whitePlayerType;
	private PlayerType blackPlayerType;
	private int searchDepth;

	private Board santoriniBoard;
	private final GameSetup gameSetup;

	private boolean firstTime = true;
	private boolean isMiddleOfMove;
	private boolean fileImported;
	private Alliance currentPLayerAlliance;
    private int destinationTilCoordinate;
    private int pieceToBeInserted;
    private int index;
	private Move lastBuildMove;

    private boolean gameOver;
	private Tile sourceTile;
	private Tile destinationTile;
	private Piece humanMovedPiece;

	private final static Dimension MINIMUM_FRAME_DIMENSION = new Dimension(500, 500);
	private final static Dimension OUTER_FRAME_DIMENSION = new Dimension(700, 700);
	private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
	private final static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

	//private static final Table INSTANCE = new Table();

	//Constructor
	public Table() {
		this.gameOver = false;
	    this.gameFrame = new JFrame("Santorini");
		this.gameFrame.setLayout(new BorderLayout());
		this.gameSetup = new GameSetup(this.gameFrame, true);
		this.santoriniBoard = Board.createStandardBoard();
		this.moveLog = new MoveLog();
		this.addObserver(new TableGameAIWatcher());
		nextButton = new JButton("Next Move");
		this.newGame(this.gameSetup);
		setupGUI();
		this.boardPanel.drawBoard(santoriniBoard);
		this.firstTime = false;
		this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.gameFrame.setVisible(true);
	}

	private void setupGUI() {
		this.index = 0;
	    //Setting the size of the frame
		this.gameFrame.setMinimumSize(MINIMUM_FRAME_DIMENSION);
		this.gameFrame.setSize(OUTER_FRAME_DIMENSION);

		//Creating and populating the menu bar
		final JMenuBar tableMenuBar = pupulateMenuBar();
		this.gameFrame.setJMenuBar(tableMenuBar);

		//Creating the board panel that holds all the tiles
		this.boardPanel = new BoardPanel();
		this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);

		//JPanel that shows which players move is
		this.playerToMove = new PlayerToMove(true);
		this.gameFrame.add(this.playerToMove, BorderLayout.NORTH);


		fixButton(nextButton);
		nextButton.setEnabled(false);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(nextButton);
		this.gameFrame.add( buttonPanel, BorderLayout.SOUTH);
	}

	private void fixButton(JButton nextButton) {
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ZIGAAAA RULESSSSS ========");
				if(fileImported){
					if(index< moveLog.size()){
						final Move move = moveLog.getNextMove();

						if(move.getMoveType() == Move.MoveType.INSERT_Move){
							if(index ==0){
								santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, move.getDestinationCoordinate(), Alliance.WHITE);
								//moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileId, Alliance.WHITE), tileId));
							}else if(index ==1){
								santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, move.getDestinationCoordinate(), Alliance.WHITE);
								currentPLayerAlliance = Alliance.BLACK;
							}else if(index == 2){
								santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, move.getDestinationCoordinate(), Alliance.BLACK);
							}else if(index ==3){
								santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, move.getDestinationCoordinate(), Alliance.BLACK);
								currentPLayerAlliance = Alliance.WHITE;
							}

						}else{
							if(move.getMoveType() == Move.MoveType.MAJOR_Move){
								MoveTransition transition;
								final Move majorMove = new Move.MajorMove(santoriniBoard, santoriniBoard.getTile(move.getSourceCoordinate()).getPiece(), move.getDestinationCoordinate());
								transition = santoriniBoard.getCurrentPlayer().makePieceMove(majorMove);
								if(transition.getMoveStatus().isDone()){
									santoriniBoard = transition.getTransitionBoard();
								}
							}else if(move.getMoveType() == Move.MoveType.BUILD_Move){
								MoveTransition transition;
								final Move buildMove = new Move.BuildMove(santoriniBoard, santoriniBoard.getTile(move.getSourceCoordinate()).getPiece(), move.getDestinationCoordinate());
								transition = santoriniBoard.getCurrentPlayer().makeBuildMove(buildMove);
								if(transition.getMoveStatus().isDone()) {
									santoriniBoard = transition.getTransitionBoard();
								}
							}
							currentPLayerAlliance = santoriniBoard.getCurrentPlayer().getAlliance();

						}


						boardPanel.drawBoard(santoriniBoard);
						index++;
					}else {
						fileImported = false;
						nextButton.setEnabled(false);
						gameOver=  false;
					}

					//santoriniBoard;

				}

			}
		});
	}

	/**
	 * This is where we create the new Game
	 * @param gameSetup
	 */
	private void newGame(GameSetup gameSetup) {
		if(!gameSetup.getWindowClosedWithX()){
			if(!gameSetup.getCancelClicked()){
				if(gameSetup.getOkayClicked()){
					//Get the game to Start
					//this.allPiecesInserted = false;
					//this.statusBar = new Label("-*.*-");
					//statusBar.setBackground(Color.GRAY);
					//this.gameFrame.add(this.statusBar, BorderLayout.SOUTH);
					nextButton.setEnabled(false);
					this.fileImported = false;
					this.gameOver = false;
					this.pieceToBeInserted =0;
					this.currentPLayerAlliance = Alliance.WHITE;
					//this.isBuildMove = false;
					this.isMiddleOfMove = false;
					this.santoriniBoard = Board.createStandardBoard();
					this.moveLog.clear();
					this.santoriniBoard.setBlackPlayerType(gameSetup.getBlackPlayerType());
					this.santoriniBoard.setWhitePlayerType(gameSetup.getWhitePlayerType());
					this.blackPlayerType = gameSetup.getBlackPlayerType();
					this.whitePlayerType= gameSetup.getWhitePlayerType();
					this.searchDepth = gameSetup.getSearchDepth();
					this.santoriniBoard.resetTileLayers();
					if(Board.getBlackPlayerType() == PlayerType.COMPUTER &&
							Board.getWhitePlayerType() == PlayerType.COMPUTER){
						insertComputerPieces();
					}

					if(Board.getWhitePlayerType() == PlayerType.COMPUTER && Board.getBlackPlayerType()==PlayerType.HUMAN){
						insertComputerWhitePieces();
					}
					if(!this.firstTime){
						this.boardPanel.drawBoard(santoriniBoard);
					}

				}
			}
		}
	}//!- NewGame

    /**
     * This is where we add all the menus to the Menu Bar
     */
	private JMenuBar pupulateMenuBar() {
		JMenuBar tableMenuBar = new JMenuBar();
		tableMenuBar.add(createFileMenu());
		tableMenuBar.add(createOptionMenu());
		return tableMenuBar;
	}

	/**
	 * This method is responsible for creating the file menu.
	 * @return fileMenu
	 */
	private JMenu createFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		
		//New Game
		final JMenuItem newGameItem = new JMenuItem("New Game");
		newGameItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Create new game");
				newGame(gameSetup);
			}
		});
		fileMenu.add(newGameItem);
		
		
			//Import file
		final JMenuItem importMenuItem = new JMenuItem("Import");
		importMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Import the FIle WIll you!");
				
				/**
				 * Create 2 sub menus, 
				 * 1 - Import game
				 * 2 - Read Game Step by step // go step by step
				 */
				JFileChooser openFile = new JFileChooser();
				openFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFile.addChoosableFileFilter(new FileNameExtensionFilter("SAN Files", "santo"));
				openFile.setAcceptAllFileFilterUsed(true);
				int ret = openFile.showDialog(gameFrame, "Open File");
				if(ret == JFileChooser.APPROVE_OPTION) {
					mainFile = openFile.getSelectedFile(); //here we get the name of the file
					String extension = getFileExtension(mainFile);
					
					if(extension.equals("santo")) {
						//santoriniBoard =
						santoriniBoard = Board.createStandardBoard();
						moveLog.readMovesFormFile(mainFile);
						fileImported = true;
						nextButton.setEnabled(true);
						gameOver= true;
						moveLog.setIndex();
						index =0;
						System.out.println("THIS IS THE IMPORTED FILE\n" + moveLog);
						boardPanel.drawBoard(santoriniBoard);
					}
				}
				
			}
		});
		fileMenu.add(importMenuItem);
		
		//Export file
		final JMenuItem exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Import the FIle WIll you!");
				//TODO
				saveFileSanto();
			}
		});
		fileMenu.add(exportMenuItem);
		
		
			//Exit program
		final JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
	
		return fileMenu;
	}//!- CreateFileMenuBar

	private void saveFileSanto() {
		// TODO Auto-generated method stub
		StringBuilder stringBuilder = new StringBuilder();
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SAN Files", "santo"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		int retrival = fileChooser.showSaveDialog(gameFrame);
		
		if(retrival == JFileChooser.APPROVE_OPTION) {
			
			try {
				
				stringBuilder.append(moveLog);
				
				File santoFile = fileChooser.getSelectedFile();

				FileWriter fw = new FileWriter(santoFile);
				fw.write(stringBuilder.toString());

				fw.close();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		
	}//!-SaveFileSanto

	/**
	 * Get the extension of the file
	 * @param file
	 * @return
	 */
    private static String getFileExtension(final File file) {
		String fileName = file.getName();
		if(fileName.lastIndexOf(".")!= -1 && 
				fileName.lastIndexOf(".")!= 0 ) {
			return fileName.substring(fileName.lastIndexOf(".")+1);
		}else {
			return "";
		}
	}//!- getFileExtension
	/**
     * The optionsMenu contains:
     * Game Setup - where we choose the players, and the search depth of MiniMax
     * @return
     */
	private JMenu createOptionMenu(){
		final JMenu optionMenu = new JMenu("Options");

		final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
		setupGameMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getGameSetup().promptUser(); //Make gameSetup visible
				newGame(getGameSetup());

				setupUpdate(getGameSetup());
				System.out.println("==========================\n=======================");
				System.out.println("Choice Made");
				//need to update the changes
			}
		});

		optionMenu.add(setupGameMenuItem);
		return optionMenu;
	}//!- createOptionMenu

    private void setupUpdate(GameSetup gameSetup) {
	    setChanged();
	    notifyObservers(gameSetup);
    }

    private class TableGameAIWatcher implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            /* if the current player is an ai player than
            we want the computer to make its move */

			Player pl = santoriniBoard.checkWinner();
            if(getGameSetup().isAIPlayer(santoriniBoard.getCurrentPlayer())&& pl==null && !(santoriniBoard.getCurrentPlayer().getActivePieces().size()<2))
                /* && game not over, winner or smth */{
					final AIThinkTank aiThinkTank = new AIThinkTank();
					aiThinkTank.execute();
            }

            if(getGameSetup().isAIPlayer(santoriniBoard.getCurrentPlayer())&& pl!= null){
				gameOver = true;
				if(pl.getAlliance() == Alliance.BLACK){
					JOptionPane.showMessageDialog(getBoardPanel(),
							"Game Over: BLACK player won.", "Game Over",
							JOptionPane.INFORMATION_MESSAGE);
					//Black player won
				}else {
					JOptionPane.showMessageDialog(getBoardPanel(),
							"Game Over: WHITE player won.", "Game Over",
							JOptionPane.INFORMATION_MESSAGE);
					//white player won
				}
			}
        }
    }

    private int generateIdToInsertPiece(){
		while(true){
			Random r = new Random();
			int low = 6;
			int high = 18;
			int result = r.nextInt(high - low) + low;
			if(santoriniBoard.getGameBoard().get(result).getPiece()==null)
				return result;
		}
	}
    private class AIThinkTank extends SwingWorker<Move, String>{
	    private  Move bestMajorMove;
	    private  Move bestBuildMove;

        @Override
        protected Move doInBackground() throws Exception {
            Board santoriniBoard1  = moveLog.executeMoveLog();
            final MiniMax miniMax= new MiniMax(searchDepth);
            miniMax.execute(santoriniBoard1, moveLog);
            bestMajorMove = miniMax.getMajorMove();
            bestBuildMove = miniMax.getBuildMove();
            return null;
        }

        @Override
        public void done(){
            final Move majorMove1 = new Move.MajorMove(santoriniBoard,
                                                        santoriniBoard.getTile(bestMajorMove.getSourceCoordinate()).getPiece(),
                                                        bestMajorMove.getDestinationCoordinate());
            MoveTransition moveTransition = santoriniBoard.getCurrentPlayer().makePieceMove(majorMove1);
            if(moveTransition.getMoveStatus().isDone()){
                moveLog.addMove(majorMove1);
                santoriniBoard = moveTransition.getTransitionBoard();
                final Move buildMove1 = new Move.BuildMove(santoriniBoard, santoriniBoard.getTile(bestBuildMove.getSourceCoordinate()).getPiece(), bestBuildMove.getDestinationCoordinate());
                MoveTransition moveTransition_board = santoriniBoard.getCurrentPlayer().makeBuildMove(buildMove1);
                if(moveTransition_board.getMoveStatus().isDone()){
                    moveLog.addMove(buildMove1);
                    santoriniBoard = moveTransition_board.getTransitionBoard();
                }
                if(santoriniBoard.getCurrentPlayer().getAlliance() == Alliance.BLACK){
                    currentPLayerAlliance =Alliance.BLACK;
                    playerToMove.setNextPlayerBlack();
                }else {
                    currentPLayerAlliance = Alliance.WHITE;
                    playerToMove.setNextPlayerWhite();
                }
            }
            //Done
            getBoardPanel().drawBoard(santoriniBoard);
            moveMadeUpdate(PlayerType.COMPUTER);
        }
    }

    private void moveMadeUpdate(PlayerType playerType) {
	    setChanged();
	    notifyObservers(playerType);
    }



	private void insertComputerWhitePieces(){
		int tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.WHITE);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.WHITE), tileID));
		tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.WHITE);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.WHITE), tileID));
		pieceToBeInserted =2;
		currentPLayerAlliance = Alliance.BLACK;
	}

	private void insertComputerPieces(){
		int tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.WHITE);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.WHITE), tileID));
		tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.WHITE);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.WHITE), tileID));

		tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.BLACK);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.BLACK), tileID));
		tileID = generateIdToInsertPiece();
		santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.BLACK);
		moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.BLACK), tileID));
		pieceToBeInserted =4;
		currentPLayerAlliance = Alliance.WHITE;
	}
	/**
     * GETTERS
     */
	private Board getGameBoard(){
		return this.santoriniBoard;
	}
    private BoardPanel getBoardPanel(){ return this.boardPanel;}
    private GameSetup getGameSetup() { return this.gameSetup; }

	/**
	 * BOARD_PANEL, it is the board that we draw on the screen
	 */
	private class BoardPanel extends JPanel{
		final List<TilePanel> boardTiles;
		
		BoardPanel(){
			super(new GridLayout(5, 5));
			this.boardTiles = new ArrayList<>();
			for(int i =0; i<BoardUtils.NUM_TILES; i++) {
				final TilePanel tilePanel = new TilePanel(this, i);
				this.boardTiles.add(tilePanel);
				add(tilePanel);
			}
			setPreferredSize(BOARD_PANEL_DIMENSION);
			validate();
		}

		void drawBoard(Board santoriniBoard) {

			System.out.println(moveLog);

			removeAll();
			for (final TilePanel boardTile :boardTiles) {
				boardTile.drawTile(santoriniBoard);
				add(boardTile);
			}

			/*
			Setting JPanel, coloring the next player
			 */
			if(currentPLayerAlliance==Alliance.WHITE){
				playerToMove.setNextPlayerWhite();
			}else {
				playerToMove.setNextPlayerBlack();
			}
			validate();
			repaint();

			Player pl = santoriniBoard.checkWinner();
			if(!(pl == null)){
				gameOver = true;

				if(pl.getAlliance() == Alliance.BLACK){
					moveLog.addMove(lastBuildMove);
					JOptionPane.showMessageDialog(getBoardPanel(),
							"Game Over: BLACK player won.", "Game Over",
							JOptionPane.INFORMATION_MESSAGE);
					//Black player won
				}else {
					moveLog.addMove(lastBuildMove);
					JOptionPane.showMessageDialog(getBoardPanel(),
							"Game Over: WHITE player won.", "Game Over",
							JOptionPane.INFORMATION_MESSAGE);
					//white player won
				}
			}
		}

		private void setAllCandidateTiles() {
			for(TilePanel t: boardTiles){
				t.selected(false);
			}
		}

		public void setCandidateTiles(Collection<Move> calculateLegalMoves, boolean b) {
			for(Move move: calculateLegalMoves){
				boardTiles.get(move.getDestinationCoordinate()).selected(b);
			}
		}


	}//!- BoardPanel



	/**
	 * =======================================================
	 * TILE_PANEL, it represents each tile of the board
	 */

	private class TilePanel extends JPanel{
		private final int tileId;
		private boolean selected;

		//Constructor
		public TilePanel(final BoardPanel boardPanel, final int i) {
			//super(new GridBagLayout());
			super(new BorderLayout());
			this.tileId = i;
			setPreferredSize(TILE_PANEL_DIMENSION);
			assignTileImage(santoriniBoard);

			//MOUSE LISTENER
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					mouseKliked();

                    /**
                     * First part of the Move: Move piece
                     * Second Part of the Move: Build
                     */
                    if(!gameOver){
                        if(!(santoriniBoard.numberOfActivePieces() < 4)){
                            if(!(isMiddleOfMove)) {

                                if(SwingUtilities.isRightMouseButton(e)) {
                                    //debugging
                                    System.out.println(">Right");
                                    sourceTile =null;
                                    destinationTile = null;
                                    humanMovedPiece = null;
                                }else if (SwingUtilities.isLeftMouseButton(e)) {
                                    //debugging
                                    System.out.println(">Left");
                                    if(sourceTile == null) {
                                        sourceTile = santoriniBoard.getTile(tileId);
                                        if(sourceTile.getPiece()!= null){
                                            if(sourceTile.getPiece().getPieceAlliance() == currentPLayerAlliance){

                                                humanMovedPiece = sourceTile.getPiece();
                                            }else humanMovedPiece = null;
                                        }
                                        if(humanMovedPiece == null) { sourceTile = null;
                                        }else {
                                            boardPanel.setCandidateTiles(humanMovedPiece.calculateLegalMoves(santoriniBoard), true);
                                        }
                                    }else  { /* IF the source tile is not null */
                                        destinationTile = santoriniBoard.getTile(tileId);
                                        System.out.println("==================================");
                                        System.out.println("SOURCE TILE: "+ sourceTile.getTileCordinate()+"-    "+ sourceTile.getLayer());
                                        System.out.println("DESTINATION  TILE: "+ destinationTile.getTileCordinate()+"-    "+ destinationTile.getLayer());
                                        System.out.println("==================================");


                                        MoveTransition transition;
                                        final Move move = new Move.MajorMove(santoriniBoard, humanMovedPiece, destinationTile.getTileCordinate());
                                        try{
                                            transition = santoriniBoard.getCurrentPlayer().makePieceMove(move);

                                        }catch(Exception ex){
                                            System.out.println("THERE IS NO STUPID PLAYER");
                                            transition = new MoveTransition(santoriniBoard, move, MoveStatus.ILEGAL_MOVE);
                                        }

                                        if(transition.getMoveStatus().isDone()){
                                            moveLog.addMove(transition.getMove());
                                            santoriniBoard = transition.getTransitionBoard();
                                            isMiddleOfMove = true;
                                            currentPLayerAlliance = santoriniBoard.getCurrentPlayer().getAlliance();
                                        }
                                        sourceTile = null;
                                        humanMovedPiece = null;

                                        destinationTilCoordinate = destinationTile.getTileCordinate() ;
                                        destinationTile = null;

                                        sourceTile = santoriniBoard.getTile(destinationTilCoordinate);
                                        if(sourceTile.getPiece()!= null){
                                            if(sourceTile.getPiece().getPieceAlliance() == currentPLayerAlliance){
                                                humanMovedPiece = sourceTile.getPiece();
                                                boardPanel.setCandidateTiles(humanMovedPiece.calculateLegalBuildSpots(santoriniBoard), true);
                                            }else humanMovedPiece = null;
                                        }
                                    }
                                }
                                //tryingggggggggg
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard(santoriniBoard);
                                        boardPanel.setAllCandidateTiles();
                                    }
                                });
                            }//!- Is middle OF move
                            else  {
                                destinationTile = santoriniBoard.getTile(tileId);
                                MoveTransition transition;
                                final Move move = new Move.BuildMove(santoriniBoard, humanMovedPiece, destinationTile.getTileCordinate());
                                transition = santoriniBoard.getCurrentPlayer().makeBuildMove(move);

                                if(transition.getMoveStatus().isDone()){
                                    //debugging

                                    System.out.println("----- Move transistion is successful");
                                    santoriniBoard = transition.getTransitionBoard();
                                    lastBuildMove = transition.getMove();
                                    moveLog.addMove(transition.getMove());

                                    //isBuildMove =  false;
                                    isMiddleOfMove = false;

                                    sourceTile = null;
                                    destinationTile = null;
                                    humanMovedPiece = null;

                                    System.out.println("==========MY OPONENETTTTTTTTTT is: "+ santoriniBoard.getCurrentPlayer().getAlliance());
                                    //santoriniBoard.setCurrentPlayer(santoriniBoard.getCurrentPlayer().getOpponent()); //= ;
                                    currentPLayerAlliance = santoriniBoard.getCurrentPlayer().getAlliance();

									SwingUtilities.invokeLater(new Runnable() {
										@Override
										public void run() {
										    if(gameSetup.isAIPlayer(santoriniBoard.getCurrentPlayer())){
										        moveMadeUpdate(PlayerType.HUMAN);
                                            }
											boardPanel.drawBoard(santoriniBoard);
										}
									});
                                }
                            }
                        }//!- AllpiecesInserted
                        else{
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                //debugging
                                System.out.println(">Left");

                                Tile pieceTile = santoriniBoard.getTile(tileId);
                                switch (pieceToBeInserted){
                                    case 0:
                                        santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileId, Alliance.WHITE);
                                        moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileId, Alliance.WHITE), tileId));
                                        pieceToBeInserted ++;
                                        break;
                                    case 1:
										if(santoriniBoard.getGameBoard().get(tileId).getPiece()==null){
											santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileId, Alliance.WHITE);
											pieceToBeInserted ++;
											moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileId, Alliance.WHITE), tileId));
											currentPLayerAlliance = Alliance.BLACK;
											if(Board.getBlackPlayerType()==PlayerType.COMPUTER){
												int tileID = generateIdToInsertPiece();
												santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.BLACK);
												moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.BLACK), tileID));
												tileID = generateIdToInsertPiece();
												santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileID, Alliance.BLACK);
												moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileID, Alliance.BLACK), tileID));
												pieceToBeInserted =4;
												currentPLayerAlliance = Alliance.WHITE;
											}

                                        }
                                        break;
                                    case 2:
										if(santoriniBoard.getGameBoard().get(tileId).getPiece()==null) {
											santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileId, Alliance.BLACK);
											moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileId, Alliance.BLACK), tileId));
											pieceToBeInserted++;
										}
                                        break;
                                    case 3:
										if(santoriniBoard.getGameBoard().get(tileId).getPiece()==null) {
											santoriniBoard = Board.createBoardInsertPiece(santoriniBoard, tileId, Alliance.BLACK);
											moveLog.addMove(new Move.InsertPieceMove(santoriniBoard, new Piece(tileId, Alliance.BLACK), tileId));
											pieceToBeInserted++;
											//allPiecesInserted = true;
											currentPLayerAlliance = Alliance.WHITE;
											if (gameSetup.isAIPlayer(santoriniBoard.getCurrentPlayer())) {
												moveMadeUpdate(PlayerType.HUMAN);
											}
										}
                                        break;
                                }

                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard(santoriniBoard);
                                        //boardPanel.setAllCandidateTiles();
                                    }
                                });
                            }
                        }
                    }//!- GameOver
				}
				@Override
				public void mouseReleased(MouseEvent e) { }

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}
			});
			validate();
		}
		//debugging
		void mouseKliked(){System.out.print("MouseKliked");}

		private void assignTileImage(Board board) {
			this.removeAll();
			String imagePath = "resources/coll1/" + board.getTile(tileId).getLName();
			if(selected){
				imagePath+="_s";
			}

			imagePath+=".png";
			ImageIcon icon = new ImageIcon(imagePath);

			// System.out.println("Tile and image:   "+board.getTile(tileId).getTileCordinate()+ " - "+ imagePath);
			JLabel label = new JLabel(icon);
			add(label);
			//setBackground(Color.CYAN);
			//add(l1);
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}

		void drawTile(final Board board) {
			assignTileImage(board);
			//validate();
			//repaint();
		}

		void selected(boolean b) {
			this.selected = b;
		}
	}//! - Tile Panel

	private class PlayerToMove extends JPanel {
		private boolean whitePlayer = true;
		private JPanel white;
		private JPanel black;
		//constructor
		PlayerToMove(boolean itsWhitesMove){
			super(new GridLayout(1,2));
			whitePlayer = itsWhitesMove;
			white = new JPanel();
			JLabel whiteLabel = new JLabel("White Player");
			white.add(whiteLabel);
			white.setBackground(Color.RED);

			black = new JPanel();
			JLabel blackLabel = new JLabel("Black Player");
			black.add(blackLabel);
			black.setBackground(Color.WHITE);

			this.add(white);
			this.add(black);
		}
		public void setNextPlayerToMove(){
			if(this.whitePlayer){
				this.whitePlayer= false;
				this.black.setBackground(Color.RED);
				this.white.setBackground(Color.WHITE);
			}else {
				this.whitePlayer= true;
				this.black.setBackground(Color.WHITE);
				this.white.setBackground(Color.RED);
			}
		}

		void setNextPlayerWhite() {
			this.whitePlayer= true;
			this.black.setBackground(Color.WHITE);
			this.white.setBackground(Color.RED);
		}

		void setNextPlayerBlack(){
			this.whitePlayer= false;
			this.black.setBackground(Color.RED);
			this.white.setBackground(Color.WHITE);
		}
	}//! - Player To Move

	private MiniMax ComputerMove(Board santoriniBoard1) {
		final MiniMax miniMax = new MiniMax(2);
		miniMax.execute(santoriniBoard1, moveLog);
		return miniMax;
	}

	public Board executeComputerMove(){
		Board santoriniBoard1  = moveLog.executeMoveLog();
		if(santoriniBoard1.getCurrentPlayer().getAlliance() == Alliance.WHITE){
		    santoriniBoard1.setWhitePlayerType(PlayerType.COMPUTER);
        }else{
            santoriniBoard1.setBlackPlayerType(PlayerType.COMPUTER);
        }
		MiniMax miniMax = ComputerMove(santoriniBoard1);
		final Move majorMove = miniMax.getMajorMove();
		final Move majorMove1 = new Move.MajorMove(santoriniBoard, santoriniBoard.getTile(majorMove.getSourceCoordinate()).getPiece(), majorMove.getDestinationCoordinate());
		MoveTransition moveTransition = santoriniBoard.getCurrentPlayer().makePieceMove(majorMove1);
        if(moveTransition.getMoveStatus().isDone()){
		    moveLog.addMove(majorMove1);
		    santoriniBoard = moveTransition.getTransitionBoard();
		    final Move buildMove  = miniMax.getBuildMove();
		    final Move buildMove1 = new Move.BuildMove(santoriniBoard, santoriniBoard.getTile(buildMove.getSourceCoordinate()).getPiece(), buildMove.getDestinationCoordinate());
		    MoveTransition moveTransition_board = santoriniBoard.getCurrentPlayer().makeBuildMove(buildMove1);
		    if(moveTransition_board.getMoveStatus().isDone()){
		        moveLog.addMove(buildMove1);
				santoriniBoard.getCurrentPlayer().setPlayerType(PlayerType.COMPUTER);
                santoriniBoard = moveTransition_board.getTransitionBoard();

            }
		}
		return santoriniBoard;
	}
	public enum PlayerType{
		HUMAN,
		COMPUTER
	}
}//!- Table
