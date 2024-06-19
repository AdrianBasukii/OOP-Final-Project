package object;

import entity.Entity;
import main.GamePanel;

public class Block_Box extends Entity {

    GamePanel gp;
    public Block_Box(GamePanel gp) {
        super(gp);

        name = "Box";
        down1 = setup("/objects/Blocks/Box", gp.tileSize, gp.tileSize);
    }
}
