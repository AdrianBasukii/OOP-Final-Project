package object;

import entity.Entity;
import main.GamePanel;

public class Item_Key extends Entity {

    GamePanel gp;
    public Item_Key(GamePanel gp) {

        super(gp);

        name = "Key";
        down1 = setup("/objects/Items/Key", gp.tileSize, gp.tileSize);

    }
}
