import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InfoBar extends JPanel {
    final int width = 37 - Grid.cols;

    final BufferedImage background = Util.loadImage("/sprites/ui/wall_pattern.png");
    final BufferedImage arrowButtonUp = Util.loadImage("/sprites/ui/arrow_button.png");
    final BufferedImage arrowButtonRight = Util.rotateClockwise90(arrowButtonUp);
    final BufferedImage arrowButtonDown = Util.rotateClockwise90(arrowButtonRight);
    final BufferedImage arrowButtonLeft = Util.rotateClockwise90(arrowButtonDown);

    final BufferedImage arrowButtonUpInactive = Util.loadImage("/sprites/ui/arrow_button_inactive.png");
    final BufferedImage arrowButtonRightInactive = Util.rotateClockwise90(arrowButtonUpInactive);
    final BufferedImage arrowButtonDownInactive = Util.rotateClockwise90(arrowButtonRightInactive);
    final BufferedImage arrowButtonLeftInactive = Util.rotateClockwise90(arrowButtonDownInactive);

    final BufferedImage heart = Util.loadImage("/sprites/ui/heart.png");
    final BufferedImage halfHeart = Util.loadImage("/sprites/ui/half-heart.png");
    final BufferedImage emptyHeart = Util.loadImage("/sprites/ui/empty-heart.png");

    final BufferedImage state = Util.loadImage("/sprites/ui/status_button.png");

    final BufferedImage font = Util.loadImage("/sprites/ui/bitmap_font_8x16.png");
    final BufferedImage box = Util.loadImage("/sprites/ui/grey_box.png");

    final BufferedImage star = Util.loadImage("/sprites/ui/action_button.png");

    final BufferedImage submit = Util.loadImage("/sprites/ui/act_button.png");

    InfoBarStatus status = InfoBarStatus.STATUS;
    Optional<Entity> currentEntity = Optional.empty();
    Grid grid;
    Map<int[], Consumer<Grid>> buttonActions = new TreeMap<>(Arrays::compare);
    Optional<Integer> currentAction = Optional.empty();



    public InfoBar(Grid grid){
        {
            this.buttonActions.put(new int[]{2,0}, (gd)->{if (grid.yOffset>0) gd.yOffset-=1;});
            this.buttonActions.put(new int[]{2,2}, (gd)->{if (grid.yOffset+Grid.rows<grid.tilesIndices.getFirst().size()) gd.yOffset+=1;});
            this.buttonActions.put(new int[]{3,1}, (gd)->{if (grid.xOffset+Grid.cols<grid.tilesIndices.size()) gd.xOffset+=1;});
            this.buttonActions.put(new int[]{1,1}, (gd)->{if (grid.xOffset>0) gd.xOffset-=1;});
            this.buttonActions.put(new int[]{0,7},  (gd)->{
                currentEntity.ifPresent(entity -> {
                    try {
                        this.currentAction = Optional.of(entity.shared.stateMap.get(entity.state).get(0));
                        grid.setSelectable_pos(entity.shared.actions.get(currentAction.get()).getValidPositions.map((func)-> func.apply(grid.getPositionOfEntity(entity).get(), grid)).orElse(List.of()));
                    } catch (Exception ignored) {}
                });
            });
            this.buttonActions.put(new int[]{0,9},  (gd)->{
                currentEntity.ifPresent(entity -> {
                    try {
                        this.currentAction = Optional.of(entity.shared.stateMap.get(entity.state).get(1));
                        grid.setSelectable_pos(entity.shared.actions.get(currentAction.get()).getValidPositions.map((func)-> func.apply(grid.getPositionOfEntity(entity).get(), grid)).orElse(List.of()));
                    } catch (Exception ignored) {}
                });
            });
            this.buttonActions.put(new int[]{0,11},  (gd)->{
                currentEntity.ifPresent(entity -> {
                    try {
                        this.currentAction = Optional.of(entity.shared.stateMap.get(entity.state).get(2));
                        grid.setSelectable_pos(entity.shared.actions.get(currentAction.get()).getValidPositions.map((func)-> func.apply(grid.getPositionOfEntity(entity).get(), grid)).orElse(List.of()));
                    } catch (Exception ignored) {}
                });
            });
            Consumer<Grid> submitAction = (gd)->{
                currentAction.ifPresent((actionIndex)->{
                    EntityAction action = currentEntity.get().shared.actions.get(actionIndex);
                    if ((action.getValidPositions.isEmpty()) || gd.selected_pos.isPresent()){
                        action.action.apply(gd.getPositionOfEntity(currentEntity.get()).get(),gd, gd.selected_pos);
                        gd.selected_pos = Optional.empty();
                        this.currentAction = Optional.empty();
                        gd.setSelectable_pos(List.of());
                        gd.setNextTurnIn(action.cost, this.currentEntity.get());
                        this.currentEntity = gd.gotoNextTurn();
                    }
                });

            };
            this.buttonActions.put(new int[]{1,17},submitAction);
            this.buttonActions.put(new int[]{2,17},submitAction);
            this.buttonActions.put(new int[]{3,17},submitAction);
        }

        setBackground(Color.GREEN);
        this.grid = grid;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int x = e.getX()/GamePanel.tileSize;
                int y = e.getY()/GamePanel.tileSize;
                int[] pos = new int[]{x, y};
                Consumer<Grid> callback = buttonActions.get(pos);
                if (callback != null){
                    callback.accept(grid);
                }
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GamePanel.tileSize*width, GamePanel.tileSize*Grid.rows);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        BiConsumer<BufferedImage, int[]> drawAt = (image, offset) -> g2d.drawImage(image, GamePanel.affine, GamePanel.tileSize * offset[0], GamePanel.tileSize * offset[1]);
        BiConsumer<Character, int[]> placeCharAt = (char_, offset) ->{
            int ascii = (int)char_ - 32;
            g2d.drawImage(font.getSubimage(8*(ascii%10),16*(ascii/10), 8, 16),GamePanel.affine, GamePanel.tileSize*offset[0]/2, GamePanel.tileSize * offset[1]);
        };
        BiConsumer<String, int[]> writeAt = (string, offset)->{
            for (char c: string.toCharArray()){
                placeCharAt.accept(c,offset);
                offset[0]++;
            }
        };
        TriConsumer<String, int[], int[]> boxWriteAt = (string, boxSize, offset)->{
            if (boxSize[0] == 1){
                if (boxSize[1] == 1){
                    drawAt.accept(box.getSubimage(48,48,16,16),new int[] {offset[0]/2,offset[1]});
                }else{
                    drawAt.accept(box.getSubimage(48,0,16,16),new int[] {offset[0]/2,offset[1]});
                    for (int i = 1; i<boxSize[1]-1; i++){
                        drawAt.accept(box.getSubimage(48,16,16,16),new int[] {offset[0]/2,offset[1] + i});
                    }
                    drawAt.accept(box.getSubimage(48,32,16,16),new int[] {offset[0]/2,offset[1] + boxSize[1] - 1});
                }
            }else if (boxSize[1] == 1){
                drawAt.accept(box.getSubimage(0,48,16,16),new int[] {offset[0]/2,offset[1]});
                for (int i = 1; i<boxSize[0]-1; i++){
                    drawAt.accept(box.getSubimage(16,48,16,16),new int[] {offset[0]/2 + i,offset[1]});
                }
                drawAt.accept(box.getSubimage(32,48,16,16),new int[] {offset[0]/2 + boxSize[0] - 1,offset[1]});
            }else{
                drawAt.accept(box.getSubimage(0,0,16,16),new int[] {offset[0]/2,offset[1]});
                for (int i = 1; i<boxSize[0]-1; i++){
                    drawAt.accept(box.getSubimage(16,0,16,16),new int[] {offset[0]/2 + i,offset[1]});
                }
                drawAt.accept(box.getSubimage(32,0,16,16),new int[] {offset[0]/2 + boxSize[0] - 1 ,offset[1]});
                for (int j = 1; j<boxSize[0]-1; j++){
                    drawAt.accept(box.getSubimage(0,16,16,16),new int[] {offset[0]/2,offset[1]});
                    for (int i = 1; i<boxSize[0]-1; i++){
                        drawAt.accept(box.getSubimage(16,16,16,16),new int[] {offset[0]/2 + i,offset[1] + j});
                    }
                    drawAt.accept(box.getSubimage(32,16,16,16),new int[] {offset[0]/2 + boxSize[0] - 1 ,offset[1] + j});
                }
                drawAt.accept(box.getSubimage(0,32,16,16),new int[] {offset[0]/2 ,offset[1] + boxSize[1] - 1});
                for (int i = 1; i<boxSize[0]-1; i++){
                    drawAt.accept(box.getSubimage(16,32,16,16),new int[] {offset[0]/2 + i,offset[1] + boxSize[1] - 1});
                }
                drawAt.accept(box.getSubimage(32,32,16,16),new int[] {offset[0]/2 + boxSize[0] ,offset[1] + boxSize[1] - 1});
            }
            int i = 0;
            List<Character> chars =  string.chars().mapToObj(j-> (char) j).toList();
            for (String substr: Util.partition(chars,boxSize[0]*2).stream().map(chararr -> chararr.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString()).toList()){
                writeAt.accept(substr,new int[]{offset[0],offset[1] + i});
                i++;
            }
        };
        BiConsumer<String,int[]> writeBoxAt = (string, offset)->{
            boxWriteAt.apply(string, new int[]{Math.min(Math.ceilDiv(string.length(),2),(width - offset[0]/2)),Math.ceilDiv(Math.ceilDiv(string.length(),2)+(offset[0]/2),width)},offset);
        };

        for (int y = 0; y < Grid.rows; y++){
            for (int x = 0; x < width; x++){
                drawAt.accept(background,new int[]{x,y});
            }
        }
        //arrows
        {
            drawAt.accept((grid.yOffset>0)?arrowButtonUp:arrowButtonUpInactive, new int[]{2,0});
            drawAt.accept(((grid.xOffset+Grid.cols)<grid.tilesIndices.size())?arrowButtonRight:arrowButtonRightInactive,new int[]{3,1});
            drawAt.accept(((grid.yOffset+Grid.rows)<grid.tilesIndices.getFirst().size())?arrowButtonDown:arrowButtonDownInactive,new int[]{2,2});
            drawAt.accept((grid.xOffset>0)?arrowButtonLeft:arrowButtonLeftInactive,new int[]{1,1});
        }

        currentEntity.ifPresent(entity -> {
            //entity sprite
            drawAt.accept(entity.shared.sprite, new int[]{2, 1});

            //health display
            for (int i = 0; i<entity.shared.maxHealth; i+=2){
                BufferedImage heartToDraw;
                if (entity.health-i>1){
                    heartToDraw = heart;
                } else if (entity.health - i == 1) {
                    heartToDraw = halfHeart;
                } else {
                    heartToDraw = emptyHeart;
                }
                drawAt.accept(heartToDraw, new int[]{(i/2),3});
            }

            //current state
            drawAt.accept(state,new int[]{0,4});
            writeBoxAt.accept(":"+entity.state, new int[]{2,4});


            //first 3 actions
            int j = 0;
            for (EntityAction action: entity.shared.stateMap.get(entity.state).stream().limit(3).map((i)->(entity.shared.actions.get(i))).toList()){
                drawAt.accept(action.icon, new int[] {0,7+(j*2)});
                writeBoxAt.accept(":"+action.name,new int[]{2,7+j*2});
                j++;
            }

            //current action
            drawAt.accept(star, new int[]{0, 13});
            currentAction.map(i->entity.shared.actions.get(i)).ifPresent((action)->{
                writeBoxAt.accept(":"+action.name, new int[]{2,13});
                action.getValidPositions.ifPresent((getValidPos)->{
                    writeBoxAt.accept("X:"+grid.selected_pos.map((i)->Integer.toString(i[0])).orElse("None"),new int[]{0,14});
                    writeBoxAt.accept("Y:"+grid.selected_pos.map((i)->Integer.toString(i[1])).orElse("None"),new int[]{0,15});
                });
                drawAt.accept(submit, new int[]{1,17});
            });
        });
    }
}
