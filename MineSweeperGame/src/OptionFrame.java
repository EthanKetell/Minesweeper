import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class OptionFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel sliderPanel = new JPanel();
	private JTextField textFieldRows = new JTextField("10", 3);
	private JTextField textFieldCols = new JTextField("10", 3);
	private ChangeListener colsListener = new ChangeListener(){
		public void stateChanged(ChangeEvent event){
			JSlider source = (JSlider) event.getSource();
			textFieldCols.setText("" + source.getValue());
		}
	};
	private ChangeListener rowsListener = new ChangeListener(){
		public void stateChanged(ChangeEvent event){
			JSlider source = (JSlider) event.getSource();
			textFieldRows.setText("" + source.getValue());
		}
	};

    
	
	
	public OptionFrame() {
		GridLayout layout = new GridLayout(0,1);
		sliderPanel.setLayout(layout);
		textFieldRows.setEditable(false);
		textFieldCols.setEditable(false);
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 5, 50, 10);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(5);
		slider.setMinorTickSpacing(1);
		addSlider(slider, "Columns:", true);
		
		slider = new JSlider(JSlider.HORIZONTAL, 5, 50, 10);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(5);
		slider.setMinorTickSpacing(1);
		addSlider(slider, "      Rows:", false);
		
		JPanel input = new JPanel();
		input.add(sliderPanel, BorderLayout.CENTER);
		
		String[] options = { "Easy", "Normal", "Hard" };
		String difficulty = (String) JOptionPane.showInputDialog(null, input,
				"New Game", -1, null, options, "Normal");
		if (difficulty != null) {
			int rows = Integer.parseInt(textFieldRows.getText());
			int cols = Integer.parseInt(textFieldCols.getText());
			int mines = 0;
			if(difficulty.equals("Easy")){
				mines = (rows*cols)/10;
			} else if(difficulty.equals("Normal")){
				mines = (rows*cols)/7;
			} else if(difficulty.equals("Hard")){
				mines = (rows*cols)/5;
			}
			MineSweeperFrame temp = new MineSweeperFrame(cols, rows, mines);
			temp.panel.setDifficulty(difficulty);
			if(MineSweeper.frame != null){
				temp.setLocation(MineSweeper.frame.getBounds().getLocation());
				temp.setVisible(true);
				MineSweeper.frame.dispose();
				temp.panel.changeImage();
				temp.panel.paintComponent(temp.panel.getGraphics());
			}
			temp.setVisible(true);
			MineSweeper.frame = temp;
			MineSweeper.timer.start();
			dispose();
		}
		if(MineSweeper.frame == null){
			System.exit(0);
		}
	}
	
	public void addSlider(JSlider s, String description, boolean cols) {
		if(cols){
			s.addChangeListener(colsListener);
		} else {
			s.addChangeListener(rowsListener);
		}
		JPanel panel = new JPanel();
		panel.add(s);
		panel.add(new JLabel(description));
		if(cols){
			panel.add(textFieldCols);
		} else {
			panel.add(textFieldRows);
		}
		sliderPanel.add(panel);
	}

}
