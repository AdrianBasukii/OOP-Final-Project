package object;
import entity.Entity;
import main.GamePanel;

public class UI_Heart extends Entity {

    public UI_Heart(GamePanel gp){
        super(gp);

        name = "Heart";
        image = setup("/UIelements/heart_full", gp.tileSize, gp.tileSize);
        image2 = setup("/UIelements/heart_half", gp.tileSize, gp.tileSize);
        image3 = setup("/UIelements/heart_empty", gp.tileSize, gp.tileSize);
    }
}
