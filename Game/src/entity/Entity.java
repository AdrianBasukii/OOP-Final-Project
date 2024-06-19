package entity;

import main.EntitySpawner;
import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity {
    GamePanel gp;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public String name; // Entity's name
    public int worldX, worldY; // Variables for position of entity with respect to the game map
    public int speed; // Variable for how fast an entity moves its position
    boolean attackState = false; // attack mode
    public boolean alive = true; // Entity alive state
    public boolean dying = false; // Entity dying state

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; // Instance variable for images of entity
    public BufferedImage attackUp1, attackUp2, attackDown1, attackDown2, attackLeft1, attackLeft2, attackRight1, attackRight2; // Instance variable for attacking images
    public String direction = "down"; // Default direction of the entity
    public Projectile projectile; // Projectile entity

    // Variables responsible for limiting animation speed
    public int spriteCounter = 0;
    public int spriteNumber = 1;
    public int deathAnimationCounter = 0;
    public int projectileCounter = 0;

    //Setting collision area (entity's area)
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    public Rectangle attackArea = new Rectangle(0,0,0,0);
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    // Limiting movement AI speed for non players
    public int actionLockCounter = 0;

    // Objects
    public BufferedImage image, image2, image3;
    public boolean collision = false;

    // Entity health
    public int maxLife;
    public int life;
    public boolean invincible = false;
    public int invincibleCounter;

    // Pathfinder
    public boolean onPath = false;

    // Setting up the entity's appearance
    public BufferedImage setup(String imageTitle, int width, int height){
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try{
            image = ImageIO.read(getClass().getResourceAsStream(imageTitle+".png"));
            image = uTool.scaleImage(image, width, height);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    // Movement and action settings for the entity
    public void setAction(){}

    // Setting entity's reaction when damaged
    public void setDamageReaction(){}

    // Dropping item when killed
    public void dropItem(Entity item){
        gp.obj[5] = item;
        gp.obj[5].worldX = worldX;
        gp.obj[5].worldY = worldY;

    }

    // Checking blocks and other entities for collision
    public void checkCollision(){
        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        gp.collisionChecker.checkObject(this, false);
        gp.collisionChecker.checkEntity(this, gp.monster);
        boolean contactPlayer = gp.collisionChecker.checkPlayer(this);

        // Key Spiders can only attack players by touching them
        if(name == "KeySpider" && contactPlayer){
            damagePlayer();
        }
    }
    public void update(){
        setAction(); // Updating the entities' actions
        checkCollision(); // Actively checking the entities' collisions

        // If entity is not colliding, it may move
        if(!collisionOn){
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
        }

        // Limiting the speed of the movement animation
        spriteCounter++;
        if(spriteCounter > 20){
            if(spriteNumber == 1){
                spriteNumber = 2;
            }
            else if(spriteNumber == 2){
                spriteNumber = 1;
            }
            spriteCounter = 0;
        }

        // Invincibility period after receiving damage
        if(invincible){
            invincibleCounter++;
            if(invincibleCounter > 40){
                invincible = false;
                invincibleCounter = 0;
            }
        }

        // Limiting projectile shots so mobs don't shoot too much
        if(projectileCounter < 60){
            projectileCounter++;
        }
    }

    // Mob damages player
    public void damagePlayer(){
        if(!gp.player.invincible){
            gp.playSFX(4); // Player hurt sound effect
            gp.player.life--; // Decreasing player's life
            gp.player.invincible = true; // Enabling the player's short invincibility period
        }
    }

    // Drawing the entity
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Check if the entity's world coordinates are within the screen area
        if(worldX + gp.tileSize> gp.player.worldX - gp.player.screenX && worldX - gp.tileSize< gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){

            // Draw the entity based on the direction its facing
            switch(direction) {
                case "up":
                    if(spriteNumber == 1){
                        image = up1;
                    }
                    else if(spriteNumber == 2){
                        image = up2;
                    }
                    break;

                case "down":
                    if(spriteNumber == 1){
                        image = down1;
                    }
                    else if(spriteNumber == 2){
                        image = down2;
                    }
                    break;

                case "left":
                    if(spriteNumber == 1){
                        image = left1;
                    }
                    else if(spriteNumber == 2){
                        image = left2;
                    }
                    break;

                case "right":
                    if(spriteNumber == 1){
                        image = right1;
                    }
                    else if(spriteNumber == 2){
                        image = right2;
                    }
                    break;
            }

            // During invisible phase, entities will have half opacity to show that they are in the invincible state
            if(invincible){
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }

            // Death animation
            if(dying){
                deathAnimation(g2);
            }

            g2.drawImage(image, screenX, screenY,null);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    // Death animation for mobs (flickering image)
    public void deathAnimation(Graphics2D g2){
        deathAnimationCounter++;

        int interval = 5;

        if(deathAnimationCounter <= interval){
            changeAlphaValue(g2, 1f);
        }

        else if(deathAnimationCounter <= interval * 2){
            changeAlphaValue(g2, 0f);
        }

        else if(deathAnimationCounter <= interval * 3){
            changeAlphaValue(g2, 1f);
        }

        else if(deathAnimationCounter <= interval * 4){
            changeAlphaValue(g2, 0f);
        }

        else if(deathAnimationCounter <= interval * 5){
            changeAlphaValue(g2, 1f);
        }

        else if(deathAnimationCounter <= interval * 6){
            changeAlphaValue(g2, 0f);
        }

        else if(deathAnimationCounter <= interval * 7){
            changeAlphaValue(g2, 1f);
        }

        else{
            // Entity dies
            dying = false;
            alive = false;
        }
    }

    // Changing the alpha value (opacity) for death animation
    public void changeAlphaValue(Graphics2D g2, float alphaValue){
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
    }

    // A-Star Pathfinding implementation
    public void searchPath(int goalCol, int goalRow){
        int startCol = (worldX + solidArea.x)/ gp.tileSize;
        int startRow = (worldY + solidArea.y)/ gp.tileSize;

        // Initializing the pathfinder
        gp.pathFinder.setNodes(startCol, startRow, goalCol, goalRow, this);

        if(gp.pathFinder.search()){
            // The coordinates it is going to move to
            int newX = gp.pathFinder.pathList.get(0).col * gp.tileSize;
            int newY = gp.pathFinder.pathList.get(0).row * gp.tileSize;

            // Entity's hit-box position
            int entityLeftX = worldX + solidArea.x;
            int entityRightX = worldX + solidArea.x + solidArea.width;
            int entityTopY = worldY + solidArea.y;
            int entityBottomY = worldY + solidArea.y + solidArea.height;

            // Based on Entity position, find the relative direction of next tile

            if(entityTopY > newY && entityLeftX >= newX && entityRightX < newX + gp.tileSize){
                // Entity is above the newY coordinate and horizontally aligned with newX.
                direction = "up";
            }

            else if(entityTopY < newY && entityLeftX >= newX && entityRightX < newX + gp.tileSize){
                // Entity is below the newY coordinate and horizontally aligned with newX.
                direction = "down";
            }

            else if(entityTopY >= newY && entityBottomY < newY + gp.tileSize){
                // Entity is vertically aligned with newY.
                if(entityLeftX > newX){
                    // Entity is to the right of newX.
                    direction = "left";
                }

                if(entityLeftX < newX){
                    // Entity is to the left of newX.
                    direction = "right";
                }
            }

            else if(entityTopY > newY && entityLeftX > newX){
                // Entity is above and to the right of the new coordinates.
                checkCollision(); // Check for collision.
                if(collisionOn){
                    // If there is a collision, move left.
                    direction="left";
                }

                else{
                    // If no collision, move up.
                    direction = "up";
                }
            }

            else if(entityTopY > newY && entityLeftX < newX){
                // Entity is above and to the left of the new coordinates.
                checkCollision(); // Check for collision.
                if(collisionOn){
                    // If there is a collision, move right.
                    direction="right";
                }

                else{
                    // If no collision, move up.
                    direction = "up";
                }
            }

            else if(entityTopY < newY && entityLeftX > newX){
                // Entity is below and to the right of the new coordinates.
                checkCollision(); // Check for collision.
                if(collisionOn){
                    // If there is a collision, move left.
                    direction="left";
                }

                else{
                    // If no collision, move down.
                    direction = "down";
                }
            }

            else if(entityTopY < newY && entityLeftX < newX){
                // Entity is below and to the left of the new coordinates.
                checkCollision(); // Check for collision.
                if(collisionOn){
                    // If there is a collision, move right.
                    direction="right";
                }

                else{
                    // If no collision, move down.
                    direction = "down";
                }
            }

            // Once entity reaches its goal, stop the pathfinder
            int nextCol = gp.pathFinder.pathList.get(0).col;
            int nextRow = gp.pathFinder.pathList.get(0).row;

            if(nextRow == goalRow && nextCol == goalCol){
                onPath = false;
            }

        }
    }
}
