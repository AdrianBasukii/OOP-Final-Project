package entity;

import main.GamePanel;

public class Projectile extends Entity{

    public Projectile(GamePanel gp){
        super(gp);
    }

    public void set(int worldX, int worldY, String direction, boolean alive){
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.alive = alive;
        this.life = this.maxLife;
    }

    public void update() {

        // Check if the projectile comes in contact with the player
        boolean contactPlayer = gp.collisionChecker.checkPlayer(this);

        // If the projectile hits player and player is not invincible, the projectile disappears and player gets damaged
        if(!gp.player.invincible && contactPlayer){
            damagePlayer();
            alive = false;
        }

        // Movement of the projectile based on assigned direction
        switch(direction){
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
        }

        // Life would always be decreased by 1 until it dies, so it does not travel across the map and cause lag issues
        life--;
        if(life <= 0){
            alive = false;
        }

        spriteCounter++;
        if(spriteCounter > 12) {
            if(spriteNumber == 1){
                spriteNumber = 2;
            }
            else if(spriteNumber == 2){
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }
    }
}
