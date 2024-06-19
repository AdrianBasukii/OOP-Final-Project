package object;

import entity.Entity;
import main.GamePanel;

public class Block_Chest extends Entity {

    GamePanel gp;
    public Block_Chest(GamePanel gp) {
        super(gp);
        name = "Chest";
        down1 = setup("/objects/Blocks/Chest", gp.tileSize, gp.tileSize);
    }
}
