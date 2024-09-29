import javax.imageio.ImageIO;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.IntStream.range;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static <T> List<List<T>> partition(Collection<T> input, int size) {

        if (size <= 0) {
            throw new IllegalArgumentException("Invalid batch size of: " + size + ". Size should be greater than zero");
        }

        @SuppressWarnings("unchecked") T[] inputArray = (T[]) input.toArray();
        return range(0, input.size())
                .boxed()
                .collect(groupingBy(index -> index / size))
                .values()
                .stream()
                .map(indices -> indices
                        .stream()
                        .map(x -> inputArray[x])
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
    public static BufferedImage loadImage(String path){
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
        return image;
    }

    public static BufferedImage rotateClockwise90(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage dest = new BufferedImage(height, width, src.getType());

        Graphics2D graphics2D = dest.createGraphics();
        graphics2D.translate((height - width) / 2, (height - width) / 2);
        graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
        graphics2D.drawRenderedImage(src, null);

        return dest;
    }
}
