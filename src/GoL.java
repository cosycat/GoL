import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by flavia on 02.03.17.
 */
public class GoL extends JPanel implements ActionListener, KeyListener, MouseListener {
	
	/**
     * GameBoard with width and height.
     * true == alife; false == dead.
     */
    boolean[][] gameBoard;
	
	/**
	 * The width of the GameBoard in Cells.
	 */
	private final int width;
	/**
	 * The height of the GameBoard in Cells.
	 */
	private final int height;
	
	/**
	 * Whether the gameworld continues left/right and top/bottom or not.
	 */
	private boolean borderless = false;
	
	/**
	 * This Game's {@link InfoPanel}.
	 */
	private InfoPanel infoPanel;
	
	/**
	 * Whether the game doesn't change anymore and therefore has ended.
	 * TODO: Gets reset (to false) if the player changes a cell (e.g. with the mouse).
	 */
	private boolean gameHasEnded = false;
	
	/**
	 * Whther the game is reapeating itself.
	 */
	private boolean gameIsRepeating = false;
	
	/**
	 * The old gameboards to check, whether the game is repeating itself.
	 */
	private ArrayList<boolean[][]> oldGameBoards = new ArrayList<>();
	
	private final int oldGameBoardsCount = 5;
	
	public boolean getPositionAt(int x, int y) {
		if (borderless) {
			while (x < 0)
				x += gameBoard.length;
			while (x >= gameBoard.length)
				x -= gameBoard.length;
			while (y < 0)
				y += gameBoard[0].length;
			while (y >= gameBoard[0].length)
				y -= gameBoard[0].length;
		} else {
			if (x < 0 || x >= gameBoard.length || y < 0 || y >= gameBoard[0].length) {
				return false;
			}
		}
        return gameBoard[x][y];
    }
	
	/**
	 * Sets the cell at the given position to the new State
	 * @param x xPosition.
	 * @param y yPosition.
	 * @param newCellState The new State.
	 */
	private void setPosition(int x, int y, boolean newCellState) {
		boolean oldCellState = getPositionAt(x,y);
	    if (x < 0 || x >= gameBoard.length || y < 0 || y >= gameBoard[0].length) {
		    System.err.println("GoL#setPosition - should not exceed gameBoardLength. Check before.");
		    System.err.println("GoL#setPosition - x: " + x + ", y: " + y);
		    return;
	    }
	    gameBoard[x][y] = newCellState;
	    if (oldCellState != newCellState) {
		    infoPanel.cellChanged(oldCellState, newCellState, true);
	    }
	    gameHasEnded = false;
	    gameIsRepeating = false;
	    repaint();
    }
	
	/**
	 * @return The total count of cells alive.
	 */
	public int getTotalCellCount() {
		int cellCount = 0;
	    for (int x = 0; x < width; x++) {
		    for (int y = 0; y < height; y++) {
			    if (gameBoard[x][y]) {
			    	cellCount++;
			    }
		    }
	    }
	    return cellCount;
    }

    /**
     * The size of a single square cell in pixels.
     */
    final int cellSize;
	
	/**
	 * The initial delay of the {@link #updateTimer}.
	 */
	private final int initialDelay = 500;
	
	/**
	 * The maximum delay of the {@link #updateTimer}.
	 */
	private final float maxDelay = 2000;
	
	/**
	 * The minimum delay of the {@link #updateTimer}.
	 */
	private final float minDelay = 1000f/32;
	
	/**
	 * The current delay of the timer, so we can store it as a float.
	 */
	private float currentDelay = initialDelay;
	
	/**
	 * The Timer responsible for updating the GoL
	 */
	Timer updateTimer = new Timer(initialDelay, this);
	
	/**
	 * The window in which this is displayed in.
	 */
	JFrame mainWindow;
	
	/**
	 * @return true if {@link #updateTimer} is paused, otherwise false.
	 */
    boolean isPaused() {
    	return updateTimer.isRunning() == false;
    }
    
    
    
    
	
	/**
	 * Uses {@link #GoL(int, int, int, int)} and sets the {@link #cellSize} to 4.
	 * @param width {@link #width}.
	 * @param height {@link #height}.
	 */
	public GoL(int width, int height) {
	    this(width, height, 4, 50);
    }
	
	/**
	 * Opens a new Window and starts simulating the Game of Life in it.
	 * The starting pattern is completely random.
	 * @param width {@link #width}.
	 * @param height {@link #height}.
	 * @param cellSize {@link #cellSize}.
	 * @param startingCellProbability The probability that a cell starts alive. Value can be 0-100 otherwise it gets clamped to this range.
	 */
	public GoL(int width, int height, int cellSize, int startingCellProbability) {
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		
		// Game Engine
        gameBoard = new boolean[width][height];
        Random r = new Random();
        int startingCellCount = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
				int i = r.nextInt(100);
				final boolean cellStatus;
				if (i < startingCellProbability) {
					cellStatus = true;
				} else {
					cellStatus = false;
				}
                gameBoard[x][y] = cellStatus;
                if (cellStatus) startingCellCount++;
            }
        }
		
		for (int i = 0; i < oldGameBoardsCount; i++) {
			oldGameBoards.add(new boolean[width][height]);
		}
		
		this.infoPanel = new InfoPanel(startingCellCount, updateTimer.getDelay());
		
		// Window
        mainWindow = new JFrame("GoL");
        mainWindow.setSize(width, height);
        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLayout(new BorderLayout());

        mainWindow.add(this, BorderLayout.CENTER);
        mainWindow.add(infoPanel, BorderLayout.PAGE_END);

        this.setBackground(Color.LIGHT_GRAY);
        this.setSize(width * cellSize, height * cellSize);
        this.setVisible(true);
        this.setFocusable(true);
        this.requestFocusInWindow();
        
        // Listeners
        this.addKeyListener(this);
        this.addMouseListener(this);
        
        // Size
        this.setPreferredSize(new Dimension(width * cellSize, height * cellSize));

        mainWindow.pack();

        updateTimer.start();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        updateBoard();
        infoPanel.gameDidUpdate();
    }
    
    private void updateBoard() {
		boolean cellsDidChange = false;
        boolean[][] newGameBoard = new boolean[gameBoard.length][gameBoard[0].length];
        for (int x = 0; x < gameBoard.length; x++) {
            for (int y = 0; y < gameBoard[0].length; y++) {
                int n = countNeighbours(x,y);
                boolean newState = false;
                final boolean oldState = gameBoard[x][y];
                switch(n) {
                    case 2:
                        if (oldState == true)
                            newState = true;
                        break;
                    case 3:
                        newState = true;
                        break;
                    default:
                        break;
                }
                newGameBoard[x][y] = newState;
                if (oldState != newState) {
	                infoPanel.cellChanged(oldState, newState, false);
	                cellsDidChange = true;
                }
            }
        }
	    updateOldGameBoards();
	    gameBoard = newGameBoard;
        this.repaint();
	    
        // If it is repeating we stop, but it is still possible to unpause the game and run it forever.
	    // Therefore we only check this if it isn't already repeating, otherwise it would still pause after every update.
        if (CurrentEqualsOldGameBoards() && gameIsRepeating == false) {
        	updateTimer.stop();
        	gameIsRepeating = true;
	        System.out.println("GoL#updateBoard CurrentEqualsOldGameBoards!" );
        }
	
        // If it doesn't change anymore we stop and only let it continue, if the user changes a cell.
	    if (cellsDidChange == false) {
		    updateTimer.stop();
		    gameHasEnded = true;
	    }
        
    }
	
	private int countNeighbours(int x, int y) {
        int n = 0;

        for (int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                if (getPositionAt(x+i, y+j))
                    n++;
            }
        }
        return n;
    }
	
	/**
	 * Sets pos 4 to pos 5, pos 3 to pos 4 etc.
	 */
	private void updateOldGameBoards() {
		//ArrayList<boolean[][]> newOldGameBoards = new ArrayList<>();
		for (int i = oldGameBoards.size() - 1; i > 0; i--) {
			oldGameBoards.set(i, oldGameBoards.get(i-1));
		}
		oldGameBoards.set(0, gameBoard);
	}
	
	/**
	 * This method comapres the current GameBoard with the old ones to check if every cells is repeating.
	 * @return true if it is repeating itself, false otherwise.
	 */
	private boolean CurrentEqualsOldGameBoards() {
		for (int i = 0; i < oldGameBoards.size(); i++) {
			if (areGameBoardsEqual(gameBoard, oldGameBoards.get(i))) {
				System.out.println("GoL#CurrentEqualsOldGameBoards FOUND REPEAT i: " + i);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Comapres two gameboards and returns true if every cell has the same value.
	 * @param gameBoard1 A GameBoard to compare.
	 * @param gameBoard2 The other GameBoard to compare.
	 * @return true if every cell is the same.
	 */
	private boolean areGameBoardsEqual(boolean[][] gameBoard1, boolean[][] gameBoard2) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (gameBoard1[x][y] != gameBoard2[x][y]) return false;
			}
		}
		return true;
	}
	
	@Override
    public void keyTyped(KeyEvent e) {
    
    }

    @Override
    public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
		    pauseUnpause();
	    } else if (e.getKeyCode() == KeyEvent.VK_Y) {
	    	decreaseSpeed();
	    } else if (e.getKeyCode() == KeyEvent.VK_X) {
	    	increaseSpeed();
	    }
    }
	
	private void decreaseSpeed() {
		currentDelay *= 2;
		if (currentDelay > maxDelay) currentDelay = maxDelay;
		updateTimer.setDelay((int) currentDelay);
		
		infoPanel.updateSpeed(currentDelay);
	}
	
	private void increaseSpeed() {
		currentDelay /= 2;
		if (currentDelay < minDelay) currentDelay = minDelay;
		updateTimer.setDelay((int) currentDelay);
		
		infoPanel.updateSpeed(currentDelay);
	}
	
	
	/**
	 * Changes the value of {@link #isPaused()}.
	 * Doesn't do anything if {@link #gameHasEnded}.
	 */
	private void pauseUnpause() {
		if (gameHasEnded) return;
		
		if (updateTimer.isRunning()) {
			updateTimer.stop();
		} else {
			updateTimer.start();
		}
	}

    @Override
    public void keyReleased(KeyEvent e) {

    }
	
	/**
	 * Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 *
	 * @param e MouseEvent.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	
	}
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 *
	 * @param e MouseEvent.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	
	}
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 *
	 * @param e MouseEvent.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() != this) {
			System.err.println("GoL#mouseReleased - wrong source");
			return;
		}
		// 1 == LMB; 3 == RMB
		if (e.getButton() == MouseEvent.BUTTON1) {
			setPosition(e.getX() / cellSize, e.getY() / cellSize, true);
		}
		else if (e.getButton() == MouseEvent.BUTTON3) {
			setPosition(e.getX() / cellSize, e.getY() / cellSize, false);
		}
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) {
	
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	
	}
	
	
	///////////// PAINTING /////////////
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int x = 0; x < gameBoard.length; x++) {
			for (int y = 0; y < gameBoard[0].length; y++) {
				if (gameBoard[x][y]) {
					drawSingleCell(g, x, y, Color.BLACK);
				} else {
					drawSingleCell(g, x, y, Color.WHITE);
				}
			}
		}
		
	}
	
	private void drawSingleCell(Graphics g, int x, int y, Color color) {
		g.setColor(color);
		g.fillRect(x*cellSize, y*cellSize, cellSize, cellSize);
		//System.out.println("drawSingleCell");
	}
	
}
