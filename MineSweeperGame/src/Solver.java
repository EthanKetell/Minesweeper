import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;


public class Solver {
	
	private static Array2D<Tile> target;
	private static Timer solve = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			for(Location loc : target.getOccupiedLocations()){
				if(target.get(loc).autoLogic()){
					((MineSweeperPanel)target.getPanel()).changeImage();
					break;
				}
			}
		}
	});

	public Solver(Array2D<Tile> array) {
		target = array;
	}
	
	public static void solve(Array2D<Tile> target){
		Solver.target = target;
		if(solve.isRunning()){
			solve.stop();
		} else {
			solve.start();
		}
	}
	
	public static boolean isSolvable(Array2D<Tile> target){
		Solver.target = target;
		Array2D<Tile> backup = copy();
		int ctw = ((MineSweeperPanel)target.getPanel()).clicksToWin;
		boolean cont = true;
		((MineSweeperPanel)target.getPanel()).testing = true;
		for(int turn = 0; turn < 500 && cont; turn++){
			cont = false;
			for(Location loc : target.getOccupiedLocations()){
				if(target.get(loc).autoLogic()){
					cont = true;
					((MineSweeperPanel)target.getPanel()).changeImage();
					((MineSweeperPanel)target.getPanel()).tick();
				}
			}
		}
		cont = ((MineSweeperPanel)target.getPanel()).isGameOver();
		((MineSweeperPanel)backup.getPanel()).gameOver = false;
		((MineSweeperPanel)backup.getPanel()).clicksToWin = ctw;
		((MineSweeperPanel)backup.getPanel()).array = backup;
		((MineSweeperPanel)backup.getPanel()).changeImage();
		((MineSweeperPanel)target.getPanel()).testing = false;
		return cont;
	}
	
	private static Array2D<Tile> copy(){
		Array2D<Tile> out = new Array2D<Tile>(target.getNumCols(), target.getNumRows());
		out.assignPanel(target.getPanel());
		for(Location loc : target.getOccupiedLocations()){
			Tile temp = new Tile();
			temp.putSelfInArray(out, loc);
			if(target.get(loc).hasMine){
				temp.hasMine = true;
			}
			if(target.get(loc).hasBeenClicked){
				temp.hasBeenClicked = true;
			}
			if(target.get(loc).flagged){
				temp.flagged = true;
			}
		}
		for(Location loc : out.getOccupiedLocations()){
			out.get(loc).countAdjacentMines();
		}
		return out;
	}
}
