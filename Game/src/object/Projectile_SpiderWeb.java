package object;

import entity.Projectile;
import main.GamePanel;

public class Projectile_SpiderWeb extends Projectile {

    GamePanel gp;

    public Projectile_SpiderWeb(GamePanel gp){

        super(gp);
        this.gp = gp;

        name = "Spiderweb";
        speed= 5;
        maxLife = 80;
        life = maxLife;
        alive = false;
        getProjectileImage();
    }

    public void getProjectileImage(){
        up1 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        up2 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        down1 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        down2 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        left1 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        left2 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        right1 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
        right2 = setup("/projectile/spider_web", gp.tileSize, gp.tileSize);
    }
}
