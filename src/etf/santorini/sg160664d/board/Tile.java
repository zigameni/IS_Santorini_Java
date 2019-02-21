//Tile.java  - Represents a tile in the board

package etf.santorini.sg160664d.board;

import java.util.HashMap;
import java.util.Map;

import etf.santorini.sg160664d.pieces.Piece;

/**
 * @author Gazmend Shehu
 */
public abstract class Tile {
	protected int tileCordinate; //coordinate of the tile: 0-24
	private static final Map<Integer, EmptyTile> EMPTY_TILES = createAllPossibleEmptyTiles();
	protected int layer;
	private boolean candidateForNextMove;
	
	//Constructor method
	public static Tile createTile(final int tileCoordinate, final Piece piece, int layer) {
		return piece != null? new OccupiedTile(tileCoordinate, piece, layer):  new EmptyTile(tileCoordinate)/*EMPTY_TILES.get(tileCoordinate)*/;
	}

	
	//Constructor
	private Tile(int tileCoordinate, int layer) {
		this.tileCordinate = tileCoordinate;
		this.layer = layer;
		this.candidateForNextMove = false;
	}

	public int getTileCordinate(){return tileCordinate;}
	public int getLayer() {return this.layer;}
	public void incLayer() {
		this.layer++;
		System.out.println("INC tile: "+this.tileCordinate+ "--- Layer: "+ this.getLayer());
		System.out.println("ICON OF THIS TILE:  "+  getLName()+".png");
	}
	public void setLayer(final int layer) {this.layer = layer;}
	//public void setCandidateForNextMove(boolean candidate){this.candidateForNextMove = candidate;}
	public String getLName() {
		String path = "";
		switch (this.layer) {
			case 0: path += "0";break;
			case 1: path += "1";break;
			case 2: path +=  "2";break;
			case 3: path +=  "3";break;
			case 4: path +=  "4";break;
			default: path= "0";
		}


		if(getPiece()!=null) {
			if(getPiece().getPieceAlliance()==Alliance.BLACK){path+="bp";}
			else {path+="wp";}
		}else {path+="np";}

		return path;
	}

	private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {
		final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();	
		for(int i =0; i <BoardUtils.NUM_TILES; i++) {
			emptyTileMap.put(i, new EmptyTile(i));
		}
		return emptyTileMap;
	}
	//Abstract Methods
	public abstract boolean isTileOccupied();
	public abstract Piece getPiece();
	
	//CLASS of EMPTY_TILE
	public static class EmptyTile extends Tile{
		private Piece piece;
		public EmptyTile(final int tileCoordinate) {
			super(tileCoordinate, 0); //Layer = 0;
			piece = null;
		}
		public EmptyTile(final int tileCoordinate, int layer){
			super(tileCoordinate, layer);
			piece = null;
		}
		@Override 
		public String toString() {
			Integer i = this.layer;
			return i.toString();
		}
		
		@Override
		public boolean isTileOccupied() {return false;}

		@Override
		public Piece getPiece() {return piece;}
	}//!- Empty_tile
	
	//CLASS of OCCUPIED_TILE
		public static final class OccupiedTile extends Tile{
			private Piece pieceOnTile;
			
			public OccupiedTile(final int tileCoordinate, Piece pieceOnTile, int layer) {
				super(tileCoordinate, layer);
				this.pieceOnTile = pieceOnTile;
			}

			@Override
			public String toString() {
				Integer i = this.layer;
				return pieceOnTile.toString()+i.toString();
			}
			
			@Override
			public boolean isTileOccupied() {return true;}

			@Override
			public Piece getPiece() {return pieceOnTile;}
		}//!- OccupiedTile
}//!-Tile
