import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.Timer;




public class MineSweeper {
	public static MineSweeperFrame frame;
	public static boolean exploding = false;
	public static Timer timer = new Timer(25, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if(exploding){
				frame.panel.gameOver(false);
			}
//			for(Location loc : frame.panel.array.getOccupiedLocations()){
//				if(frame.panel.array.get(loc).autoLogic()){
//					frame.panel.changeImage();
//				}
//			}
			frame.panel.tick();
			frame.panel.repaint();
		}
	});

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tile.initializeImages();
		new OptionFrame();
	}
	
	public static  void repeatSettings(){
		MineSweeperFrame temp = new MineSweeperFrame(frame.cols, frame.rows, frame.mines);
		temp.setBounds(frame);
		temp.panel.changeImage();
		temp.panel.paintComponent(temp.panel.getGraphics());
		temp.panel.setDifficulty(frame.panel.getDifficulty());
		temp.setVisible(true);
		frame.dispose();
		frame = temp;
	}
	
	public static JMenuBar makeMenus(JFrame frame){
		JMenuBar mBar = new JMenuBar();
		JMenu menu = new JMenu("Mine Sweeper");
		mBar.add(menu);
		JMenu submenu = new JMenu("New Game");
		menu.add(submenu);
		JMenuItem menuItem = new JMenuItem("Same Settings");
		submenu.add(menuItem);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, 
		        KeyEvent.META_DOWN_MASK));
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				repeatSettings();
			}
		});
		if(!(frame instanceof MineSweeperFrame)){
			menuItem.setEnabled(false);
		}
		menuItem = new JMenuItem("New Settings...");
		submenu.add(menuItem);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_N, 
		        KeyEvent.META_DOWN_MASK + 
		        KeyEvent.SHIFT_DOWN_MASK));
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new OptionFrame();
			}
		});
		menuItem = new JMenuItem("Quit");
		menu.add(menuItem);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, 
		        KeyEvent.META_DOWN_MASK));
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		frame.setJMenuBar(mBar);
		return mBar;
	}
}
