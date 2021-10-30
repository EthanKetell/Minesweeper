import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.*;


public class MineSweeperFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MineSweeperPanel panel;
	int rows ,cols, mines;
	private GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice[] devices = g.getScreenDevices();
	public final int displayWidth = devices[0].getDisplayMode().getWidth();
	public final int displayHeight = devices[0].getDisplayMode().getHeight();
	
	int borderWidth = 15;
	
	public MineSweeperFrame(int cols, int rows, int mines){
		super("Mine Sweeper");
		MineSweeper.makeMenus(this);
		setMinimumSize(new Dimension(240+borderWidth*3, 240+44+borderWidth+((2*borderWidth)+49)));
		this.rows = rows;
		this.cols = cols;
		this.mines = mines;
		panel = new MineSweeperPanel(cols, rows, mines, this);
		add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		int width = panel.width*6/10 + borderWidth*2;
		int height = panel.height*6/10 + 44 + borderWidth + ((2*borderWidth)+49);
		if(width > displayWidth){
			width = displayWidth;
		}
		if(height > displayHeight){
			height = displayHeight;
		}
		this.setBounds(0, 0, width, height);
		this.setBackground(new Color(40,50,50));
	}
	
	public int getWidth(){
		return getBounds().width;
	}
	
	public int getHeight(){
		return getBounds().height;
	}
	
	public MineSweeperPanel getPanel(){
		return panel;
	}

	public void setBounds(MineSweeperFrame modelFrame){
		Rectangle frameSize = modelFrame.getBounds();
		int deltaX = modelFrame.getPanel().deltaX;
		int deltaY = modelFrame.getPanel().deltaY;
		int scale = modelFrame.getPanel().scale;
		setBounds(frameSize);
		getPanel().deltaX = deltaX;
		getPanel().deltaY = deltaY;
		getPanel().scale = scale;
	}
	
}
