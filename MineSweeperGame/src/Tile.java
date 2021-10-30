import imageManipulator.ImageManipulator;
import imageManipulator.NamedImage;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Tile implements Cloneable{
	public boolean hasMine, hasBeenClicked, flagged;
	private Boolean mineClicked;
	private MineSweeperPanel panel;
	private int sideMines;
	private int frame;
	private Location loc;
	private Array2D<Tile> array;
	public static int highestDelay = Integer.MAX_VALUE;
	private static boolean radialExplosions = true;
	private static ArrayList<NamedImage> allImages;
	private String lastImageName;
	
	public Tile() {
		flagged = false;
		hasBeenClicked = false;
		hasMine = false;
		sideMines = 0;
		frame = 11;
	}
	
 	public static void initializeImages(){
		allImages = new ArrayList<NamedImage>();
		String[] names = {
				"Mine_true.png",
				"Mine_false.png",
				"Clear_0.png",
				"Clear_1.png",
				"Clear_2.png",
				"Clear_3.png",
				"Clear_4.png",
				"Clear_5.png",
				"Clear_6.png",
				"Clear_7.png",
				"Clear_8.png",
				"Hidden_true.png",
				"Hidden_false.png",
				"Explosion_1.png",
				"Explosion_2.png",
				"Explosion_3.png",
				"Explosion_4.png",
				"Explosion_5.png",
				"Explosion_6.png",
				"Explosion_7.png",
				"Explosion_8.png",
				"Explosion_9.png",
				"Explosion_10.png",
				"Explosion_11.png"
		};
		BufferedImage[] images = ImageManipulator.getImages(names);
		for(int scale = 2; scale <= 100; scale += 2){
			int spot = 0;
			for(BufferedImage image : images){
				allImages.add(new NamedImage(ImageManipulator.scale(image, scale, false), names[spot] + "Scaled "+scale+"%"));
				spot++;
			}
		}
	}
	
	public boolean autoLogic(){
		boolean didSomething = false;
		if(hasBeenClicked && !((MineSweeperPanel)panel).isGameOver()){
			int flagged = 0;
			ArrayList<Tile> unflagged = new ArrayList<Tile>();
			for(Location loc : this.loc.getAdjacentLocations()){
				if(array.isValid(loc)){
					if(((Tile)array.get(loc)).flagged){
					flagged++;
					} else {
						unflagged.add((Tile)array.get(loc));
					}
				}
			}
			if(flagged >= sideMines && flagged != 0){
				for(Tile tile : unflagged){
					if(!tile.hasBeenClicked){
						didSomething = true;
					}
					if(tile.hasMine()){
						tile.click(true);
					} else {
						tile.click(false);
					}
				}
			} else {
				int sidesUnclicked = 0;
				ArrayList<Location> unclickedNeigbors = new ArrayList<Location>();
				for(Location loc : this.loc.getAdjacentLocations()){
					if(array.isValid(loc) && !array.get(loc).hasBeenClicked){
						sidesUnclicked++;
						unclickedNeigbors.add(loc);
					}
				}
				if(sidesUnclicked == sideMines){
					for(Location loc : unclickedNeigbors){
						if(!array.get(loc).flagged){
							didSomething = true;
							array.get(loc).flagged = true;
						}
					}
				}
			}
		}
		return didSomething;
	}
	
	public String toString(){
		return "Tile at "+loc;
	}
	
	public void tickFrame(){
		if(frame <= 10){
			frame++;
			if(frame == 5){
				click(false);
			}
		}
	}
	
	public BufferedImage getImage(int scale){
		String name = "";
		if(hasBeenClicked){
			if(hasMine){
				name = "Mine_"+mineClicked+".png" + "Scaled "+scale+"%";
			} else {
				name = "Clear_"+sideMines+".png" + "Scaled "+scale+"%";
			}
		} else {
			name = "Hidden_"+flagged+".png" + "Scaled "+scale+"%";
		}
		if(name != lastImageName){
			lastImageName = name;
			return allImages.get(allImages.indexOf(new NamedImage(null, name))).getImage();
		} else {
			return null;
		}
	}
	
	public boolean isExploding(){
		if(frame > 0){
			return true;
		}
		return false;
	}
	
	public BufferedImage getExplosionFrame(int scale){
		String name = "Explosion_11.png" + "Scaled "+scale+"%";
		if(hasMine && frame > 0){
			name = "Explosion_"+frame+".png" + "Scaled "+scale+"%";
		}
		return allImages.get(allImages.indexOf(new NamedImage(null, name))).getImage();
	}
	
	public void giveMine(){
		hasMine = true;
	}
	
	public Tile getNextTile(){
		for(int direc = 0; direc < 8; direc++){
			Location tempLoc = loc.getAdjacentLocation(direc);
			if(array.isValid(tempLoc)){
				Tile temp = (Tile)array.get(tempLoc);
				if(temp != null && !temp.hasBeenClicked){
					return temp;
				}
			}
		}
		return null;
	}
	
	public void countAdjacentMines(){
		sideMines = 0;
		for(Location loc : getLocation().getAdjacentLocations()){
			if(array.isValid(loc) && ((Tile)array.get(loc)).hasMine()){
				sideMines++;
			}
		}
	}
	
	public void click(boolean hardwareClick){
		if(!hasBeenClicked){
			panel.clicksToWin--;
			hasBeenClicked = true;
			flagged = false;
			if(hasMine){
				if(mineClicked == null)
					mineClicked = hardwareClick;
				if(hardwareClick){
					for(Location loc : array.getOccupiedLocations()){
						if(radialExplosions){
							if(((Tile)array.get(loc)).hasMine){
								int delay = -(loc.distanceFrom(this.loc)+10);
								((Tile)array.get(loc)).frame = delay;
								if(((Tile)array.get(loc)).mineClicked != null && ((Tile)array.get(loc)).mineClicked){
									((Tile)array.get(loc)).frame = 0;
								}
								if(((Tile)array.get(loc)).frame < Tile.highestDelay){
									Tile.highestDelay = ((Tile)array.get(loc)).frame;
								}
							}
						} else {
							if(((Tile)array.get(loc)).hasMine){
								Random gen = new Random();
								((Tile)array.get(loc)).frame = -(gen.nextInt((int)Math.sqrt(array.getNumCols()*array.getNumRows()))+15);
								if(((Tile)array.get(loc)).frame < highestDelay){
									highestDelay = ((Tile)array.get(loc)).frame;
								}
								if(((Tile)array.get(loc)).mineClicked != null && ((Tile)array.get(loc)).mineClicked){
									((Tile)array.get(loc)).frame = 0;
								}
							}
						}
					}
					frame = 0;
					MineSweeper.exploding = true;
				}
			}
			if(!hasMine){
				if(sideMines == 0){
					Tile temp = getNextTile();
					while(temp != null){
						temp.click(true);
						temp = getNextTile();
					}
				}
			}
		}
	}
	
	public void toggleFlag(){
		if(!hasBeenClicked){
			flagged = !flagged;
		}
	}
	
	public boolean hasBeenClicked(){
		return hasBeenClicked;
	}
	
	public boolean hasMine(){
		return hasMine;
	}
	
	public Array2D<Tile> getArray(){
		return array;
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public void putSelfInArray(Array2D<Tile> array, Location loc){
		this.loc = loc;
		this.array = array;
		panel = (MineSweeperPanel) array.getPanel();
		if(panel == null){
			System.out.println("WTF");
		}
		array.add(this, loc);
	}

}
