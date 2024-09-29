import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.BiConsumer;

public class TopBar extends JPanel {
    static Tuple<BufferedImage, InfoBarStatus>[] items = new Tuple[]{
            new Tuple(Util.loadImage("/sprites/ui/status_button.png"), InfoBarStatus.STATUS),
    };

    InfoBar info;

    public TopBar(InfoBar info){
        this.info = info;
        this.setBackground(Color.GRAY);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getX()/GamePanel.tileSize<items.length){
                    info.status = items[e.getX()/GamePanel.tileSize].y;
                }
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.getParent().getWidth(), GamePanel.tileSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        BiConsumer<BufferedImage, int[]> drawAt = (image, offset) -> g2d.drawImage(image, GamePanel.affine, GamePanel.tileSize * offset[0], GamePanel.tileSize * offset[1]);
        int i = 0;
        for (Tuple item: items){
            drawAt.accept((BufferedImage) item.x,new int[]{i,0});
            i++;
        }
    }
}
