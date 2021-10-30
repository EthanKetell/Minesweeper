import imageManipulator.ImageManipulator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;



public class MineSweeperPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Array2D<Tile> array;
	private int initialX, initialY, startDeltaX, startDeltaY, 
				explodeFrames, activeMillis;
	private final int maxScale;
	public int deltaX, deltaY, scale, activeSeconds;
	public int clicksToWin;
	public final int width, height, mines, rows, cols;
	private String difficulty;
	private BufferedImage panelImage;
	private MineSweeperFrame homeFrame;
	private Location mousePos;
	private boolean running, won;
	public boolean testing, gameOver, addMines;
	private ArrayList<Location> notHere;
	boolean check = true;
	int timesRemade = 0;
	boolean solvable = false;
	boolean checking = false;
	
	public Timer solver = new Timer(100, new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			solveOnce();
		}
	});
	
	public Timer checker = new Timer(5, new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if(check){
				solvable = Solver.isSolvable(array);
			} else {
				if(solvable || timesRemade == 100){
					if(!solvable){
						 JOptionPane.showMessageDialog(null, "Could not find solvable board,\ngame will require guessing","WARNING", -1);
					}
					activeSeconds = 0;
					checking = false;
					paintComponent(getGraphics());
					checker.stop();
				} else {
					timesRemade++;
					addMines(notHere, mines);
					array.get(mousePos).click(true);
				}
			}
			check = !check;
		}
	});
	int borderWidth = 15;
	Color background = new Color(40,50,50);
	Color infoDisplay = new Color(160,170,170);
	
	public MineSweeperPanel(int c, int r, int m, MineSweeperFrame frame){
		setFocusable(true);
		testing = false;
		setBackground(background);
		rows = r;
		cols = c;
		mines = m;
		gameOver = false;
		addMines = true;
		running = false;
		explodeFrames = 0;
		scale = 100;
		deltaX = 0;
		deltaY = 0;
		setIgnoreRepaint(true);
		setUpGrid(c, r);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		setVisible(true);
		width = array.getNumCols()*50;
		height = array.getNumRows()*50;
		while(((width*scale)/100)*((height*scale)/100) > 3062500){
			scale -= 2;
		}
		maxScale = scale;
		if(scale > 60){
			scale = 60;
		}
		homeFrame = frame;
	}
	
	public boolean isGameOver(){
		return gameOver;
	}
	
	public boolean setDifficulty(String value){
		if(difficulty == null){
			difficulty = value;
			return true;
		} else {
			return false;
		}
	}
	
	public String getDifficulty(){
		return difficulty;
	}
	
	public void tick(){
		if(running){
			if(!gameOver){
				if(clicksToWin == 0){
					gameOver(true);
				}
				if(activeSeconds != 9999)
					activeMillis += 25;
				if(activeMillis == 1000){
					activeMillis = 0;
					activeSeconds++;
				}
			}
		}
	}
	
	private void setUpGrid(int c, int r) {
		array = new Array2D<Tile>(c, r);
//		System.out.println(rows+", "+cols+", "+mines);
		clicksToWin = (cols*rows)-mines;
		array.assignPanel(this);
		for(Location loc : array.getValidLocations()){
			Tile temp = new Tile();
			temp.putSelfInArray(array, loc);
		}
	}
	
	public void gameOver(boolean win){
		gameOver = true;
		won = win;
		if(!testing){
			mousePos = new Location(-1,-1);
			String[] options = {"New Settings", "Same Settings", "Quit"};
			int replay = -1;
			if(win){
				paintComponent(getGraphics());
				replay = JOptionPane.showOptionDialog(null,"You took "+activeSeconds+" seconds to beat a "+cols+"x"
						+rows+" "+difficulty+" game.\nStart a new Game?","You Won!", JOptionPane.YES_NO_CANCEL_OPTION, 
						JOptionPane.QUESTION_MESSAGE, ImageManipulator.getIcon("Win.png"), options, options[0]);
			} else {
				if(explodeFrames > Tile.highestDelay-11){
					explodeFrames --;
					for(Location loc : array.getOccupiedLocations()){
						array.get(loc).tickFrame();
					}
				} else {
					MineSweeper.exploding = false;
					paintComponent(getGraphics());
					replay = JOptionPane.showOptionDialog(null,"Start a new Game?",
							"You Lose!", JOptionPane.YES_NO_CANCEL_OPTION, 
							JOptionPane.QUESTION_MESSAGE, ImageManipulator.getIcon("Lose.png"), options, options[0]);
				}
			}
			if(replay == 0){
				new OptionFrame();
			}
			if(replay == 1){
				MineSweeper.repeatSettings();
			}
			if(replay == 2){
				System.exit(0);
			}
		}
	}
	
	private void addMines(ArrayList<Location> notHere, int m){
		activeSeconds = 0;
		activeMillis = 0;
		for(Location loc : array.getOccupiedLocations()){
			array.get(loc).hasMine = false;
			array.get(loc).hasBeenClicked = false;
			array.get(loc).flagged = false;
		}
		for(int mine = 0; mine < m; mine++){
			Location temp = array.getRandomOccupiedLocation();
			while(notHere.indexOf(temp) != -1 || array.get(temp).hasMine()){
				temp = array.getRandomOccupiedLocation();
			}
			array.get(temp).giveMine();
		}
		for(Location loc : array.getOccupiedLocations()){
			array.get(loc).countAdjacentMines();
		}
		clicksToWin = (rows*cols)-mines;
		addMines = false;
	}
	
	public void changeImage(){
		if(checking)
			return;
		panelImage = new BufferedImage(width*scale/100 + (borderWidth*2), height*scale/100 + borderWidth + ((borderWidth*2)+49), BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = panelImage.getGraphics();
		for(Location loc : array.getOccupiedLocations()){
			BufferedImage temp = array.get(loc).getImage(scale);
			if(temp != null){
				if(loc.equals(mousePos)){
					temp = ImageManipulator.colorOverlay(temp, Color.WHITE, 50, false);
				}
				g2.drawImage(temp, loc.getCol()*50*scale/100 + borderWidth, loc.getRow()*50*scale/100 + ((borderWidth*2)+49), null);
			}
		}
		if(MineSweeper.exploding) {
			for(Location loc : array.getOccupiedLocations()){
				BufferedImage temp = array.get(loc).getExplosionFrame(scale);
				g2.drawImage(temp, loc.getCol()*50*scale/100 + borderWidth, loc.getRow()*50*scale/100 + ((borderWidth*2)+49), null);
			}
		}
	}
	
	public BufferedImage drawNumbers(int maxDigits, String number){
		BufferedImage out = new BufferedImage(maxDigits*30, 50, BufferedImage.TYPE_INT_ARGB);
		while(number.length() < maxDigits){
			number = " " + number;
		}
		char[] digits = number.toCharArray();
		Graphics2D g2d = out.createGraphics();
		int x = 0;
		for(char digit : digits){
			g2d.drawImage(ImageManipulator.get("Digital_"+digit+".png"), x*30, 0, null);
			x++;
		}
		g2d.dispose();
		return out;
	}
	
	public void makeInfoDisplay(BufferedImage image){
		Graphics2D g2d = image.createGraphics();
		int height = image.getHeight();
		int width = image.getWidth();
		//Main Frame Color
		g2d.setColor(infoDisplay);
		g2d.fillRect(0, 0, width, ((borderWidth*2)+49));
		g2d.fillRect(0, 0, borderWidth, height);
		g2d.fillRect(width-borderWidth, 0, borderWidth, height);
		g2d.fillRect(0, height-borderWidth, width, borderWidth);
		//Highlights
		g2d.setColor(new Color(255,255,255,75));
		g2d.fillRect(0, 0, width, 5);
		g2d.fillRect(borderWidth-5, borderWidth+49, 130, 5);
		g2d.fillRect(width-borderWidth-125, borderWidth+49, 130, 5);
		g2d.fillRect(borderWidth+120, borderWidth-5, 5, 59);
		g2d.fillRect(width-borderWidth, borderWidth-5, 5, 59);
		g2d.fillRect(0, 0, 5, height);
		g2d.fillRect(borderWidth-5, height-borderWidth, width-borderWidth*2+10, 5);
		g2d.fillRect(width-borderWidth, ((borderWidth*2)+49)-5, 5, height-((borderWidth*2)+49)-borderWidth+10);
		//Shadows
		g2d.setColor(new Color(0,0,0,25));
		g2d.fillRect(borderWidth-5, borderWidth-5, 130, 5);
		g2d.fillRect(width-borderWidth-125, borderWidth-5, 130, 5);
		g2d.fillRect(borderWidth-5, borderWidth-5, 5, 59);
		g2d.fillRect(width-borderWidth-125, borderWidth-5, 5, 59);
		g2d.fillRect(0, height-5, width, 5);
		g2d.fillRect(width-5, 0, 5, height);
		g2d.fillRect(borderWidth-5, ((borderWidth*2)+49)-5, width-borderWidth*2+10, 5);
		g2d.fillRect(borderWidth-5, ((borderWidth*2)+49)-5, 5, height-((borderWidth*2)+49)-borderWidth+10);
		g2d.dispose();
	}
	
	@Override
	public void repaint(){
		if(checking)
			return;
		else
			super.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		changeImage();
		if(checking)
			return;
		if(g == null){
			return;
		}
		BufferedImage finalImage = new BufferedImage(homeFrame.getWidth(), homeFrame.getHeight()-44, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = finalImage.createGraphics();
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, finalImage.getWidth(), finalImage.getHeight());
		g2d.drawImage(panelImage, deltaX, deltaY, null);
		makeInfoDisplay(finalImage);
		if(gameOver && !won){
			g2d.drawImage(drawNumbers(4, "Err"), borderWidth, borderWidth, null);
		} else {
			g2d.drawImage(drawNumbers(4, ""+clicksToWin), (((borderWidth*2)+49)-50)/2, borderWidth, null);
		}
		g2d.drawImage(drawNumbers(4, ""+activeSeconds), finalImage.getWidth()-120-borderWidth, borderWidth, null);
		g2d.dispose();
		g.drawImage(finalImage, 0, 0, null);
	}

//	private void resetGrid(ArrayList<Location> notHere){
//		setUpGrid(cols, rows);
//		addMines(notHere, mines);
//		array.get(mousePos).click(true);
//		changeImage();
//		paintComponent(getGraphics());
//	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(checking)
			return;
		if (gameOver) {
			return;
		}
		if (array.isValid(mousePos)) {
			if (arg0.getButton() == 3 || arg0.isMetaDown()) {
				array.get(mousePos).toggleFlag();
			} else if (arg0.getButton() == 1) {
				if (addMines) {
					notHere = mousePos.getAdjacentLocations();
					notHere.add(mousePos);
					addMines(notHere, mines);
					checking = true;
					checker.start();
				}
				running = true;
				if (!array.get(mousePos).hasBeenClicked){
					array.get(mousePos).click(true);
				} else {
					array.get(mousePos).autoLogic();
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(checking)
			return;
		mousePos = new Location(-1, -1);
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(homeFrame == null){
			return;
		}
		if(gameOver || array.isValid(mousePos)){
			if(!arg0.isAltDown()){
				deltaX = startDeltaX + arg0.getXOnScreen() -  initialX;
			}
			if(!arg0.isShiftDown()){
				deltaY = startDeltaY + arg0.getYOnScreen() -  initialY;
			}
			
			if(homeFrame.getWidth() > panelImage.getWidth()){
				if(deltaX > homeFrame.getWidth()-panelImage.getWidth()){
					deltaX = homeFrame.getWidth()-panelImage.getWidth();
				}
				if(deltaX < 0){
					deltaX = 0;
				}
			} else {
				if(deltaX < homeFrame.getWidth()-panelImage.getWidth()){
					deltaX = homeFrame.getWidth()-panelImage.getWidth();
				}
				if(deltaX > 0){
					deltaX = 0;
				}
			}
			if(homeFrame.getContentPane().getHeight() > panelImage.getHeight()){
				if(deltaY > homeFrame.getContentPane().getHeight()-panelImage.getHeight()){
					deltaY = homeFrame.getContentPane().getHeight()-panelImage.getHeight();
				}
				if(deltaY < 0){
					deltaY = 0;
				}
			} else {
				if(deltaY < homeFrame.getContentPane().getHeight()-panelImage.getHeight()){
					deltaY = homeFrame.getContentPane().getHeight()-panelImage.getHeight();
				}
				if(deltaY > 0){
					deltaY = 0;
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		initialX = arg0.getXOnScreen();
		initialY = arg0.getYOnScreen();
		startDeltaX = deltaX;
		startDeltaY = deltaY;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(checking)
			return;
		mouseMoved(arg0);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(checking)
			return;
		if(gameOver){
			mousePos = new Location(-1, -1);
			return;
		}
		int colPixel = (arg0.getX()-deltaX-borderWidth);
		int rowPixel = (arg0.getY()-deltaY-((borderWidth*2)+49));
		if(colPixel < 0 || rowPixel < 0){
			mousePos = new Location(-1, -1);
			return;
		}
		int col = colPixel/(50*scale/100);
		int row = rowPixel/(50*scale/100);
		mousePos = new Location(col, row);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		if(homeFrame == null || arg0.getWheelRotation() == 0){
			return;
		}
		if(!mousePos.equals(new Location(-1,-1))){
			mousePos = new Location(-1,-1);
		}
		if(arg0.isMetaDown()){
			if(arg0.getWheelRotation() > 0){
				scale -= 2;
			} else {
				scale += 2;
			}
			if(scale > maxScale){
				scale = maxScale;
			}
			if(scale < 2){
				scale = 2;
			}
		} else {
			if(arg0.isShiftDown() && !arg0.isAltDown()){
				if(arg0.getWheelRotation() > 0){
					deltaX -= 3;
				} else {
					deltaX += 3;
				}
			} else {
				if(arg0.getWheelRotation() > 0){
					deltaY -= 3;
				} else {
					deltaY += 3;
				}
			}
			if(homeFrame.getWidth() > panelImage.getWidth()){
				if(deltaX > homeFrame.getWidth()-panelImage.getWidth()){
					deltaX = homeFrame.getWidth()-panelImage.getWidth();
				}
				if(deltaX < 0){
					deltaX = 0;
				}
			} else {
				if(deltaX < homeFrame.getWidth()-panelImage.getWidth()){
					deltaX = homeFrame.getWidth()-panelImage.getWidth();
				}
				if(deltaX > 0){
					deltaX = 0;
				}
			}
			if(homeFrame.getContentPane().getHeight() > panelImage.getHeight()){
				if(deltaY > homeFrame.getContentPane().getHeight()-panelImage.getHeight()){
					deltaY = homeFrame.getContentPane().getHeight()-panelImage.getHeight();
				}
				if(deltaY < 0){
					deltaY = 0;
				}
			} else {
				if(deltaY < homeFrame.getContentPane().getHeight()-panelImage.getHeight()){
					deltaY = homeFrame.getContentPane().getHeight()-panelImage.getHeight();
				}
				if(deltaY > 0){
					deltaY = 0;
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_SPACE){
			if(e.isShiftDown()){
				solver.start();
			} else {
				solveOnce();
			}
		}
	}
	
	private void solveOnce(){
		for(Location loc : array.getOccupiedLocations()){
			if(array.get(loc).autoLogic()){
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
