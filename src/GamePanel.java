import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel implements Runnable {
    static final int originalTileSize = 16;
    static final int scale = 3;
    static final int tileSize = originalTileSize * scale;


    Thread gameThread;
    final int FPS = 60;
    static final AffineTransformOp affine = new AffineTransformOp(new AffineTransform(scale, .0, .0, scale, .0, .0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

    Grid grid;
    InfoBar infoBar;
    TopBar topBar;

    GamePanel() {
        this.setBackground(Color.BLUE);
        this.setDoubleBuffered(true);
        FlowLayout layout = (FlowLayout)this.getLayout();
        layout.setHgap(0);
        layout.setVgap(0);
        this.setLayout(layout);
        this.grid = new Grid();
        this.infoBar = new InfoBar(grid);
        this.topBar = new TopBar(infoBar);
        JPanel separatorTop = new JPanel(){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getParent().getWidth(), 13);
            }
        };
        JPanel separatorSide = new JPanel(){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, getParent().getHeight());
            }
        };

        separatorTop.setBackground(Color.BLACK);
        separatorSide.setBackground(Color.BLACK);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(grid, BorderLayout.WEST);
        centerPanel.add(separatorSide, BorderLayout.CENTER);
        centerPanel.add(infoBar, BorderLayout.EAST);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(separatorTop,BorderLayout.CENTER);
        topPanel.add(centerPanel,BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.add(topBar, BorderLayout.NORTH);
        this.add(topPanel, BorderLayout.CENTER);

        //testing stuff DON'T LEAVE IN HERE
        infoBar.currentEntity = grid.getEntityAtPosition(3,7);
    }

    public void startGameThread() {
        this.gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double deltaTime = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (this.gameThread != null) {
            currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime) / 1000000000.0;
            lastTime = currentTime;
            if (deltaTime >= (1.0 / FPS)) {
                this.update(deltaTime);
                this.repaint();
                deltaTime -= (1.0 / FPS);
            }
        }
    }

    public void update(double deltaTime) {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
