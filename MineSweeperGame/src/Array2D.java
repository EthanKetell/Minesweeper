import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;


public class Array2D<E> {
	private Object[][] array;
	private JPanel hostPanel;
	private int numRows, numCols;
	
	public Array2D(int cols, int rows){
		numRows = rows;
		numCols = cols;
		array = new Object[cols][rows];
	}
	
	public void assignPanel(JPanel panel){
		hostPanel = panel;
	}
	
	public JPanel getPanel(){
		return hostPanel;
	}
	
	@SuppressWarnings("unchecked")
	private E array(int col, int row){
		return (E)array[col][row];
	}
	
	public String toString(){
		return "Array2D of size ("+numCols+", "+numRows+")";
	}
	
	public int getNumCols(){
		return numCols;
	}
	
	public int getNumRows(){
		return numRows;
	}
	
	public boolean isValid(Location loc){
		if(loc.getCol() >= numCols || loc.getCol() < 0 || loc.getRow() >= numRows || loc.getRow() < 0){
			return false;
		} else {
			return true;
		}
	}
	
	public ArrayList<Location> getValidLocations(){
		ArrayList<Location> out = new ArrayList<Location>();
		for(int col = 0; col < numCols; col++){
			for(int row = 0; row < numRows; row++){
				out.add(new Location(col, row));
			}
		}
		return (ArrayList<Location>)out;
	}
	
	public ArrayList<Location> getOccupiedLocations(){
		ArrayList<Location> out = new ArrayList<Location>();
		for(Location loc : getValidLocations()){
			if(get(loc) != null){
				out.add(loc);
			}
		}
		return out;
	}
	
	public ArrayList<Location> getEmptyLocations(){
		ArrayList<Location> out = new ArrayList<Location>();
		for(Location loc : getValidLocations()){
			if(get(loc) == null){
				out.add(loc);
			}
		}
		return out;
	}
	
	public E remove(Location loc) throws IndexOutOfBoundsException{
		if(isValid(loc)){
			E temp = get(loc);
			array[loc.getCol()][loc.getRow()] = null;
			return temp;
		} else {
			throw new IndexOutOfBoundsException(loc+" is not on "+this);
		}
	}
	
	public boolean add(E obj, Location loc) throws IndexOutOfBoundsException{
		if(isValid(loc)){
			if(get(loc) == null){
				array[loc.getCol()][loc.getRow()] = obj;
				return false;
			} else {
				return true;
			}
		} else {
			throw new IndexOutOfBoundsException(loc+" is not on "+this);
		}
	}
	
	public E get(Location loc) throws IndexOutOfBoundsException{
		if(isValid(loc)){
			return array(loc.getCol(), loc.getRow());
		} else {
			throw new IndexOutOfBoundsException(loc+" is not on "+this);
		}
	}
	
	public Location getRandomOccupiedLocation(){
		Random gen = new Random();
		ArrayList<Location> occLoc = getOccupiedLocations();
		return occLoc.get(gen.nextInt(occLoc.size()));
	}
	
	public Location getRandomEmptyLocation(){
		Random gen = new Random();
		ArrayList<Location> occLoc = getEmptyLocations();
		return occLoc.get(gen.nextInt(occLoc.size()));
	}
	
}
