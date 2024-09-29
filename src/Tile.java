import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Tile {
    public BufferedImage image;
    public boolean passable;
    public Optional<String> desc;

    public Tile(BufferedImage image, boolean passable, Optional<String> desc) {
        this.image = image;
        this.passable = passable;
        this.desc = desc;
    }

    public Tile(String path, boolean passable, Optional<String> desc) {
        BufferedImage image = null;
        try (InputStream is = Tile.class.getResourceAsStream(path)) {
            if (is != null) {
                image = ImageIO.read(is);
            } else {
                System.err.println("Resource not found: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.image = image;
        this.passable = passable;
        this.desc = desc;
    }

    public static final Tile Grass;
    public static final Tile Brick;

    static {
        Grass = loadTile("/sprites/tiles/grass.png", true, Optional.of("Grass tile"));
        Brick = loadTile("/sprites/tiles/brick.png", false, Optional.of("Brick tile"));
    }

    private static Tile loadTile(String path, boolean passable, Optional<String> desc) {
        return new Tile(Util.loadImage(path), passable, desc);
    }
}
