package main;
import javax.swing.JFrame;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Creating the window settings
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close game
        window.setResizable(false); // Not allow resizing
        window.setTitle("Little Adventures"); // Title of window

        // Creating a canvas for the game's graphical content (gamePanel)
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBackground(new Color(37, 19, 26));
        window.add(gamePanel); // adding gamePanel as the content of the windows

        // Adjusting other window settings
        window.pack(); // sizes the window so that all its contents are at or above their preferred sizes
        window.setLocationRelativeTo(null); // Centers window on the screen
        window.setVisible(true); // Makes the window visible

        gamePanel.setupGame();

        // Starting the loop of updating information and displaying the updated graphics
        gamePanel.startGameThread();
    }
}
