import java.util.ArrayList;


public class Location {
	private int col, row;
	public final static int northwest = 0;
	public final static int north = 1;
	public final static int northeast = 2;
	public final static int east = 3;
	public final static int southeast = 4;
	public final static int south = 5;
	public final static int southwest = 6;
	public final static int west = 7;
	
	public Location(int col, int row){
		this.col = col;
		this.row = row;
	}
	
	@Override
	public String toString(){
		return "Location ("+col+", "+row+")";
	}
	
	@Override
	public boolean equals(Object other){
		if(this == other){
			return true;
		}
		if(other == null){
			return false;
		}
		if(other.getClass() != this.getClass()){
			return false;
		}
		if(((Location)other).getCol() == getCol() && ((Location)other).getRow() == getRow()){
			return true;
		} else {
			return false;
		}
	}
	
	public int distanceFrom(Location other){
		int a = Math.abs(other.getCol() - getCol());
		int b = Math.abs(other.getRow() - getRow());
		int c = (int) (Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)) + 0.5);
		return c;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public int makeValidDirection(int direction){
		while(direction > 7){
			direction -= 8;
		}
		while(direction < 0){
			direction += 8;
		}
		return direction;
	}
	
	public Location getAdjacentLocation(int direction) throws IndexOutOfBoundsException{
		if(0 > direction || direction > 7){
			throw new IndexOutOfBoundsException(direction+" is not a valid direction.");
		}
		if(direction == 0){
			return new Location(col-1, row-1);
		}
		if(direction == 1){
			return new Location(col, row-1);
		}
		if(direction == 2){
			return new Location(col+1, row-1);
		}
		if(direction == 3){
			return new Location(col+1, row);
		}
		if(direction == 4){
			return new Location(col+1, row+1);
		}
		if(direction == 5){
			return new Location(col, row+1);
		}
		if(direction == 6){
			return new Location(col-1, row+1);
		}
		if(direction == 7){
			return new Location(col-1, row);
		}
		return null;
	}
	
	public ArrayList<Location> getAdjacentLocations(){
		ArrayList<Location> out = new ArrayList<Location>();
		for(int direc = 0; direc < 8; direc ++){
			out.add(getAdjacentLocation(direc));
		}
		return out;
	}
}
