import java.awt.image.BufferedImage;
import java.util.*;

public class EntityShared {
    public BufferedImage sprite;
    Map<String, List<Integer>> stateMap;
    List<EntityAction> actions;
    public int maxHealth;
    public int agility;

    public static EntityShared Me = new EntityShared("/sprites/entities/me.png", Map.of("Idle", List.of(0,1,2)),new ArrayList<>(Arrays.asList(EntityAction.MoveOne, EntityAction.Wait, EntityAction.Heal)));

    EntityShared(String path, Map<String, List<Integer>> stateMap, List<EntityAction> actions){
        this.actions = actions;
        this.stateMap = stateMap;
        this.sprite = Util.loadImage(path);
        this.maxHealth = 8;
        this.agility = 3;
    }
}
