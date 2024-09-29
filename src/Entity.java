import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class Entity {
    public int health;
    public String name;
    public String state;
    boolean controlable;

    public EntityShared shared;

    public Entity(String name, int health, String state, EntityShared shared, boolean controlable){
        this.name = name;
        this.state = state;
        this.shared = shared;
        this.health = health;
        this.controlable = controlable;
    }

    public void heal(int health){
        this.health += health;
        this.health = Math.max(this.health, this.shared.maxHealth);
    }

    static public Entity Me(){
        return new Entity("Me", 8,"Idle",EntityShared.Me, true);
    }
}
