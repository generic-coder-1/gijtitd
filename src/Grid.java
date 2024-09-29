import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grid extends JPanel {
    List<List<Integer>> tilesIndices;
    List<Tile> tiles;
    List<List<Optional<Integer>>> entities;
    List<Entity> entitiyList;
    int xOffset = 0;
    int yOffset = 0;
    List<int[]> selectable_pos;
    Optional<int[]> selected_pos = Optional.empty();

    public final static int cols = 32;
    public final static int rows = 18;
    final static int gridWidth = cols * GamePanel.tileSize;
    final static int gridHeight = rows * GamePanel.tileSize;
    final BufferedImage validOverlay = Util.loadImage("/sprites/tiles/valid_overlay.png");
    final BufferedImage selectedOverlay = Util.loadImage("/sprites/tiles/selected_overlay.png");

    int turnNumber = 0;
    HashMap<Integer,Queue<Entity>> turnOrder = new HashMap<>();

    public Grid() {
        setPreferredSize(new Dimension(gridWidth,gridHeight));
        this.tiles = Arrays.asList(Tile.Grass, Tile.Brick);
        this.tilesIndices = Stream.generate(() -> Stream.generate(()->0).limit(rows + 10).collect(Collectors.toList())).limit(cols + 1).collect(Collectors.toList());
        this.entities = Stream.generate(()->Stream.generate(Optional::<Integer>empty).limit(rows + 10).collect(Collectors.toList())).limit(cols + 1).collect(Collectors.toList());
        this.entitiyList = new ArrayList<>();
        this.selectable_pos = List.of();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int[] mpos = new int[] {e.getX()/GamePanel.tileSize,e.getY()/GamePanel.tileSize};
                selectable_pos.stream().filter(p->(p[0] == mpos[0] && p[1] == mpos[1])).findAny().ifPresent(p->{
                    selected_pos = Optional.of(p);
                });
            }
        });

        //don't leave in
        this.setEntityAtPosition(3,7, Optional.of(Entity.Me()));
        this.getEntityAtPosition(3,7).get().health-=3;
        tilesIndices.get(3).set(7, 1);
    }

    public void setNextTurnIn(int turns, Entity entity){
        this.turnOrder.putIfAbsent(this.turnNumber + turns, new LinkedList<>());
        Queue<Entity> moveList = this.turnOrder.get(this.turnNumber + turns);
        moveList.add(entity);
//        int i = 0;
//        while (moveList.get(i).shared.agility<=entity.shared.agility){
//            i++;
//            if (i == moveList.size()){
//                moveList.add(entity);
//                return;
//            }
//        }
//        moveList.add(i, entity);
    }

    public Optional<Entity> gotoNextTurn(){
        if (turnOrder.isEmpty()){
            return Optional.empty();
        }
        while (!this.turnOrder.containsKey(this.turnNumber)){
            this.turnNumber++;
        }
        Entity movingEntity = turnOrder.get(turnNumber).remove();
        if (turnOrder.get(turnNumber).isEmpty()){
            turnOrder.remove(turnNumber);
        }
        return Optional.of(movingEntity);
    }

    public void setSelectable_pos(List<int[]> selectable_pos) {
        this.selectable_pos = selectable_pos;
        this.selected_pos = Optional.empty();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GamePanel.tileSize*cols, GamePanel.tileSize*rows);
    }

    public Optional<Entity> getEntityAtPosition(int x, int y){
        if (x>cols || y>rows){
            return Optional.empty();
        }
        return this.entities.get(x).get(y).map((i)->entitiyList.get(i));
    }

    public void setEntityAtPosition(int x, int y, Optional<Entity> entity){
        if (entity.isPresent()){
            if (!this.entitiyList.contains(entity.get())) {
                this.entitiyList.add(entity.get());
            }
            this.entities.get(x).set(y,Optional.of(this.entitiyList.size()-1));
        }else{
            this.entities.get(x).set(y, Optional.empty());
        }
    }

    public Optional<int[]> getPositionOfEntity(Entity e){
        int index = entitiyList.indexOf(e);
        int i = 0;
        int j = 0;
        for (List<Optional<Integer>> col: entities){
            for (Optional<Integer> cel: col){
                if (cel.map(k->k==index).orElse(false)){
                    return Optional.of(new int[] {i,j});
                }
                j++;
            }
            j = 0;
            i++;
        }
        return Optional.empty();
    }

    public Optional<Tile> getTileAtPosition(int x, int y){
        if (x>cols || y>rows){
            return Optional.empty();
        }
        return Optional.of(this.tiles.get(this.tilesIndices.get(x).get(y)));
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        BiConsumer<BufferedImage, int[]> drawAt = (image, offset) -> g2d.drawImage(image, GamePanel.affine, GamePanel.tileSize * (offset[0] - xOffset), GamePanel.tileSize * (offset[1] - yOffset));
        for (int colNum = 0; colNum<this.tilesIndices.size(); colNum++){
            for (int rowNum = 0; rowNum<this.tilesIndices.get(colNum).size(); rowNum++){
                drawAt.accept(tiles.get(tilesIndices.get(colNum).get(rowNum)).image,new int[]{colNum,rowNum});
            }
        }
        for (int colNum = 0; colNum<this.entities.size(); colNum++){
            for (int rowNum = 0; rowNum<this.entities.get(colNum).size(); rowNum++){
                if (this.getEntityAtPosition(colNum,rowNum).isPresent()){
                    drawAt.accept(this.getEntityAtPosition(colNum,rowNum).get().shared.sprite, new int[]{colNum,rowNum});
                }
            }
        }
        for (int[] validPos: this.selectable_pos){
            drawAt.accept(validOverlay,validPos);
        }
        this.selected_pos.ifPresent(pos -> {
            drawAt.accept(selectedOverlay, pos);
        });
    }
}
