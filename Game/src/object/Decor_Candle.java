package object;

import entity.Entity;
import main.GamePanel;

public class Decor_Candle extends Entity {

    GamePanel gp;
    public Decor_Candle(GamePanel gp) {
        super(gp);

        this.gp = gp;

        name = "Candle";
        down1 = setup("/objects/Decor/candlestick_1", gp.tileSize, gp.tileSize);
    }
}
