import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("testing!");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBorder(BorderFactory.createMatteBorder(30,30,30,30, Color.BLACK));
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setLocation(0,0);
        window.setVisible(true);
        gamePanel.startGameThread();

        //System.out.println(window.getWidth() + ", " + window.getHeight());
    }
}