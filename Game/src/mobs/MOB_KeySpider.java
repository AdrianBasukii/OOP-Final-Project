package mobs;

import entity.Entity;
import main.GamePanel;
import object.Projectile_SpiderWeb;

import java.awt.*;
import java.util.Random;

public class MOB_KeySpider extends Entity {
    GamePanel gp;
    public MOB_KeySpider(GamePanel gp){
        super(gp);

        this.gp = gp;

        name = "KeySpider";
        speed = 2;
        maxLife = 5;
        life = maxLife;
        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 30;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        projectile = new Projectile_SpiderWeb(gp);

        getMobImage();
    }

    public void getMobImage(){
        up1 = setup("/mobs/spiderKEY_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/mobs/spiderKEY_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/mobs/spiderKEY_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/mobs/spiderKEY_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/mobs/spiderKEY_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/mobs/spiderKEY_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/mobs/spiderKEY_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/mobs/spiderKEY_right_2", gp.tileSize, gp.tileSize);
    }

    public void update(){
        super.update();

        // Finding distance from player
        int playerDistanceX = Math.abs(worldX - gp.player.worldX);
        int playerDistanceY = Math.abs(worldY - gp.player.worldY);
        int playerDistance = (playerDistanceX + playerDistanceY)/gp.tileSize;

        // If player comes close to the mob, pathfinding activates
        if(!onPath && playerDistance < 5){
            int i = new Random().nextInt(100)+1;
            if(i>50){
                onPath = true;
            }
        }

        // If player moves away, pathfinding is turned off
        else if(onPath && playerDistance > 10){
            onPath = false;
        }

    }

    public void setAction(){

        // Monster being aggro towards player
        if(onPath){

            // Setting up the pathfinding to target the player
            int goalCol = (gp.player.worldX + gp.player.solidArea.x)/gp.tileSize;
            int goalRow = (gp.player.worldY + gp.player.solidArea.y)/gp.tileSize;
            searchPath(goalCol, goalRow);

            // Increase speed instead of shooting projectiles
            speed = 3;

        }
        // Random movement when monster not aggro
        else {

            speed = 2; // Normal speed
            actionLockCounter++; // A counter that limits the speed of movement change

            if (actionLockCounter == 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;

                if (i <= 25) {
                    direction = "up";
                } else if (i <= 50) {
                    direction = "down";
                } else if (i <= 75) {
                    direction = "left";
                } else {
                    direction = "right";
                }

                actionLockCounter = 0;
            }
        }
    }

    // What happens when the mob is attacked by player
    public void setDamageReaction(){
        actionLockCounter = 0;
        direction = gp.player.direction; // Moving away from the player's direction
    }
}
