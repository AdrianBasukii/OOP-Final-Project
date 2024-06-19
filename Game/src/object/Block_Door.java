package object;

import entity.Entity;
import main.GamePanel;

public class Block_Door extends Entity {

    GamePanel gp;
    public Block_Door(GamePanel gp, String doorDirection, boolean isTreasureDoor) {
        super(gp);

        // Distinguishing normal doors and treasure doors
        if(isTreasureDoor){
            name = "TreasureDoor";
        }
        else{
            name = "Door";
        }
        collision = true;

        // Solid area

        solidArea.x = 0;
        solidArea.y = 16;
        solidArea.width = 48;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Change image based on inputted door directions

        if(doorDirection == "up"){
            down1 = setup("/objects/Blocks/Door_Side_Top", gp.tileSize, gp.tileSize);
        }
        else if(doorDirection == "down"){
            down1 = setup("/objects/Blocks/Door_Side_Bottom", gp.tileSize, gp.tileSize);
        }
        else if(doorDirection == "left"){
            down1 = setup("/objects/Blocks/Door_Front_Left", gp.tileSize, gp.tileSize);
        }
        else if(doorDirection == "right"){
            down1 = setup("/objects/Blocks/Door_Front_Right", gp.tileSize, gp.tileSize);
        }
    }
}
