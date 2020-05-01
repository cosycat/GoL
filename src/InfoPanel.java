import javax.swing.*;
import java.awt.*;

/**
 * Stores the stats and displays them.
 *
 * Created by flavia on 22.08.17.
 */
public class InfoPanel extends JPanel {
	
	/**
	 * How many updates have passed since the beginning of the game.
	 */
	private int updatesPassed = 0;
	
	/**
	 * Displays {@link #updatesPassed}.
	 */
	private JLabel updatesPassedValueLabel;
	
	private JLabel updatesPassedTextLabel = new JLabel("Updates Passed: ");
	
	/**
	 * How many cells there are currently.
	 */
	private int getCellCount() {
		return startCellCount + cellsGrown - cellsDied;
	}
	
	/**
	 * Displays {@link #getCellCount()}.
	 */
	private JLabel cellCountValueLabel;
	
	private JLabel cellCountTextLabel = new JLabel("Total Cells: ");
	
	/**
	 * How many Cells where alive at the start of the game.
	 */
	private final int startCellCount;
	
	/**
	 * How many Cells died during this game.
	 */
	private int cellsDied = 0;
	
	/**
	 * Displays {@link #cellsDied}.
	 */
	private JLabel cellsDiedValueLabel = new JLabel("0");
	
	private JLabel cellsDiedTextLabel = new JLabel("Cells Died: ");
	
	/**
	 * How many Cells came alive during this game.
	 */
	private int cellsGrown = 0;
	
	/**
	 * Displays {@link #cellsDied}.
	 */
	private JLabel cellsGrownValueLabel = new JLabel("0");
	
	private JLabel cellsGrownTextLabel = new JLabel("Cells Grown: ");
	
	
	/**
	 * Displays {@link #cellsDied}.
	 */
	private JLabel updateSpeedValueLabel;
	
	private JLabel updateSpeedTextLabel = new JLabel("Updates per second: ");
	
	
	
	public InfoPanel(int startCellCount, int startDelay) {
		this.startCellCount = startCellCount;
		
		updatesPassedValueLabel = new JLabel(String.valueOf(updatesPassed));
		cellCountValueLabel = new JLabel(String.valueOf(getCellCount()));
		updateSpeedValueLabel = new JLabel(getUPSfromDelay(startDelay));
		
		// All the content.
		this.setLayout(new GridLayout(5, 2));
		
		this.add(cellCountTextLabel);
		this.add(cellCountValueLabel);
		
		this.add(cellsGrownTextLabel);
		this.add(cellsGrownValueLabel);
		
		this.add(cellsDiedTextLabel);
		this.add(cellsDiedValueLabel);
		
		this.add(updatesPassedTextLabel);
		this.add(updatesPassedValueLabel);
		
		this.add(updateSpeedTextLabel);
		this.add(updateSpeedValueLabel);
		
		
		this.setBackground(Color.lightGray);
		this.setVisible(true);
	}
	
	/**
	 * Called whenever the game updated.
	 */
	public void gameDidUpdate() {
		updatesPassedValueLabel.setText(String.valueOf(++updatesPassed));
		updateCellCountLabel();
		this.repaint();
	}
	
	public void updateCellCountLabel() {
		cellCountValueLabel.setText(String.valueOf(getCellCount()));
	}
	
	/**
	 * Called from the {@link GoL} whenever a cell changed.
	 * @param oldState The old state of the cell.
	 * @param newState The new state of the cell.
	 * @param updateLabel Whether the Label should update (when the cell changed outside of an update cycle (e.g. per mouse click).
	 */
	public void cellChanged(boolean oldState, boolean newState, boolean updateLabel) {
		if (oldState && !newState) {
			cellsDiedValueLabel.setText(String.valueOf(++cellsDied));
		} else if (!oldState && newState) {
			cellsGrownValueLabel.setText(String.valueOf(++cellsGrown));
		}
		
		if (updateLabel) updateCellCountLabel();
	}
	
	/**
	 * Updates the label which displays the game's speed ({@link #updateSpeedValueLabel}).
	 * @param timerDelay The current Delay of the {@link GoL#updateTimer}.
	 */
	public void updateSpeed(float timerDelay) {
		updateSpeedValueLabel.setText(getUPSfromDelay(timerDelay));
	}
	
	/**
	 * @param delay The Delay
	 * @return Updates per Second.
	 */
	public static String getUPSfromDelay(float delay) {
		float updatePerSecond = 1000f / delay;
		return String.valueOf(updatePerSecond);
	}
	
}
