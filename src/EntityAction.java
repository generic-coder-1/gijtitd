import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.BiFunction;

public class EntityAction {
    public String name;
    public int cost;
    public Optional<BiFunction<int[],Grid, List<int[]>>> getValidPositions;
    public TriConsumer<int[],Grid,Optional<int[]>> action;
    public BufferedImage icon = Util.loadImage("/sprites/ui/no_icon.png");
    public boolean immediate = true;


    static EntityAction MoveOne = new EntityAction(){
        {
            this.name = "Move";
            this.cost = 1;
            this.icon = Util.loadImage("/sprites/ui/move_icon.png");
            this.immediate = true;
            this.getValidPositions = Optional.of((Epos, grid) -> {
                int[][] allPositions = new int[Grid.rows * Grid.cols][2];
                Arrays.setAll(allPositions, (i)->new int[]{i/ Grid.cols, i% Grid.cols});
                ArrayList<int[]> possiblePositions = new ArrayList<int[]>(Arrays.stream(allPositions).toList());

                possiblePositions.removeIf((pos)->(Math.abs(pos[0]-Epos[0]) > 1) || (Math.abs(pos[1]-Epos[1]) > 1));
                possiblePositions.removeIf((pos)->(!grid.getTileAtPosition(pos[0],pos[1]).map(e->e.passable).orElse(false)));
                possiblePositions.removeIf((pos)->(grid.getEntityAtPosition(pos[0],pos[1]).isPresent()));

                return possiblePositions;
            });
            this.action = (pos, grid, pickedPos)->{
                int[] gridPos = pickedPos.get();
                Optional<Entity> temp = grid.getEntityAtPosition(pos[0],pos[1]);
                grid.setEntityAtPosition(pos[0],pos[1],grid.getEntityAtPosition(gridPos[0], gridPos[1]));
                grid.setEntityAtPosition(gridPos[0], gridPos[1], temp);
            };
        }
    };
    static EntityAction Wait = new EntityAction(){
        {
            this.name = "Wait";
            this.cost = 1;
            this.immediate = true;
            this.getValidPositions = Optional.empty();
            this.action = (pos, grid, pickedPos)->{};
        }
    };
    static EntityAction Heal = new EntityAction(){
        {
            this.name = "Heal";
            this.cost = 1;
            this.immediate = false;
            this.getValidPositions = Optional.empty();
            this.action = (pos, grid, pickedPos)->{grid.getEntityAtPosition(pos[0],pos[1]).get().heal(2);};
        }
    };

}
