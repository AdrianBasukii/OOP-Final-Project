package entity;

import main.EntitySpawner;
import main.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Player extends Entity{

    GamePanel gp;
    KeyHandler keyH;

    // Variables for player position on the screen
    public final int screenX;
    public final int screenY;

    // Random life steal effect for player
    private Random randomLifesteal = new Random();

    // Check if player obtained key
    public boolean keyObtained = false;

    public Player(GamePanel gp, KeyHandler keyH){
        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2); // For positioning player at the center of X
        screenY = gp.screenHeight/2 - (gp.tileSize/2); // For positioning player at the center of Y

        // Defining the player's collision area (hit-box)
        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Attack range
        attackArea.width = 36;
        attackArea.height = 36;

        setDefaultValues();
        getPlayerImage();
        getPlayerAttackingImage();
    }

    // Player starting position, speed, and default direction
    public void setDefaultValues(){
        keyObtained = false;
        attackState = false;
        invincible = false;
        life = 6;
        maxLife = 6;
        speed=6;
        direction = "down";
        worldX=gp.tileSize*6;
        worldY=gp.tileSize*25;
    }

    // Getting player movement images from resources folder
    public void getPlayerImage(){
        up1 = setup("/player/player_up_1", gp.tileSize, gp.tileSize);
        up2 = setup("/player/player_up_2", gp.tileSize, gp.tileSize);
        down1 = setup("/player/player_down_1", gp.tileSize, gp.tileSize);
        down2 = setup("/player/player_down_2", gp.tileSize, gp.tileSize);
        left1 = setup("/player/player_left_1", gp.tileSize, gp.tileSize);
        left2 = setup("/player/player_left_2", gp.tileSize, gp.tileSize);
        right1 = setup("/player/player_right_1", gp.tileSize, gp.tileSize);
        right2 = setup("/player/player_right_2", gp.tileSize, gp.tileSize);
    }

    // Getting player attacking images from resources folder
    public void getPlayerAttackingImage(){
        attackUp1 = setup("/player/player_attack_up_1", gp.tileSize, gp.tileSize*2);
        attackUp2 = setup("/player/player_attack_up_2", gp.tileSize, gp.tileSize*2);
        attackDown1 = setup("/player/player_attack_down_1", gp.tileSize, gp.tileSize*2);
        attackDown2 = setup("/player/player_attack_down_2", gp.tileSize, gp.tileSize*2);
        attackLeft1 = setup("/player/player_attack_left_1", gp.tileSize*2, gp.tileSize);
        attackLeft2 = setup("/player/player_attack_left_2", gp.tileSize*2, gp.tileSize);
        attackRight1 = setup("/player/player_attack_right_1", gp.tileSize*2, gp.tileSize);
        attackRight2 = setup("/player/player_attack_right_2", gp.tileSize*2, gp.tileSize);
    }

    // Updating position of player based on key inputs
    public void update(){

        // Player attack
        if(gp.keyH.spacePressed){
            attackState = true;
        }
        if(attackState){
            playerAttackingState();
        }

        // Movement
        else if(keyH.downPressed || keyH.upPressed || keyH.leftPressed || keyH.rightPressed) {

            // Changing directions based on key presses
            if(keyH.upPressed){
                direction = "up";
            }
            if(keyH.downPressed){
                direction = "down";
            }
            if(keyH.leftPressed){
                direction = "left";
            }
            if(keyH.rightPressed){
                direction = "right";
            }

            // COLLISION
            // Checking tile collision
            collisionOn = false; // Default collision status
            gp.collisionChecker.checkTile(this);

            // Checking object collision
            int objIndex = gp.collisionChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // Check mob collision
            int mobIndex = gp.collisionChecker.checkEntity(this, gp.monster);
            mobContact(mobIndex);

            // False value for collision will allow player to keep moving
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
        }

        // Invincibility period after receiving damage
        if(invincible){
            invincibleCounter++;
            if(invincibleCounter == 60){
                invincible = false;
                invincibleCounter = 0;
            }
        }

        // Player dies
        if(life <= 0){
            gp.stopMusic(); // Stops background music
            gp.playSFX(7); // Game over sound effect
            gp.gameState = gp.gameOverState;
        }
    }

    // Method for when a player comes in contact with a mob
    public void mobContact(int index){
        if(index !=999){
            if(!invincible){
                super.onPath = true;
            }
        }
    }

    // Method for damaging a mob
    public void damageMob(int index){
        if(index !=999){
            if(!gp.monster[index].invincible){

                gp.monster[index].life--;
                gp.monster[index].invincible = true;
                gp.monster[index].setDamageReaction();

                if (gp.monster[index].life <= 0) {
                    gp.monster[index].dying = true;
                    gp.uinterface.addScrollingMessage(gp.monster[index].name+" Killed!");

                    // 75 percent chance to regain health from killing mobs
                    int randomLifestealValue = randomLifesteal.nextInt(100) +1;
                    if(randomLifestealValue > 25){
                        if(life <= 4 && life > 1){
                            gp.uinterface.addScrollingMessage(" +2 HEALTH");
                            life+=2;
                        }
                        else if(life == 1){
                            gp.uinterface.addScrollingMessage(" +3 HEALTH");
                            life+=3;
                        }
                    }
                }
            }
        }
    }

    public void playerAttackingState(){
        spriteCounter++; // Changing the player attack images based on the counter

        if(spriteCounter <= 5){
            spriteNumber = 1;
        }
        else if(spriteCounter > 5 && spriteCounter <= 25){
            spriteNumber = 2;

            // Saving original position before temporarily moving hit-box position for attacking
            int originalWorldX = worldX;
            int originalWorldY = worldY;
            int solidAreaWidth = solidArea.width;
            int solidAreaHeight = solidArea.height;

            // Change world X and Y values based on direction
            switch(direction){
                case "up": worldY -= attackArea.height; break;
                case "down": worldY += attackArea.height; break;
                case "left": worldX -= attackArea.width; break;
                case "right": worldX += attackArea.width; break;

            }

            // Changing player's solid area to the attack area (making it larger)
            solidArea.width = attackArea.width;
            solidArea.height = attackArea.height;

            // Check if the attack hits a mob (collision)
            int mobIndex = gp.collisionChecker.checkEntity(this, gp.monster);
            damageMob(mobIndex);

            // Reverting the world X and Y, as well as the solid area to the non-attacking state
            worldX = originalWorldX;
            worldY = originalWorldY;
            solidArea.width = solidAreaWidth;
            solidArea.height = solidAreaHeight;
        }
        else if(spriteCounter > 25){
            gp.playSFX(3); // Player hitting mob sound effect
            spriteNumber = 1;
            spriteCounter = 0;
            attackState = false; // Stops player from attacking
        }
    }

    // Method for actions when picking up objects
    public void pickUpObject(int i){
        if(i != 999){
            String objectName = gp.obj[i].name; // Getting object's name
            switch(objectName){

                // Actions when picking up object
                case "Key":
                    if(!keyObtained){
                        gp.playSFX(8); // Object Pickup sound effect

                        gp.uinterface.addScrollingMessage("Key Obtained!"); // adding a message to the side

                        keyObtained = true; // key is now obtained
                        gp.obj[i] = null; // remove the key from the map
                    }
                    break;

                case "TreasureDoor":
                    if(keyObtained){
                        gp.playSFX(2); // Door Unlocked Sound Effect
                        gp.uinterface.addScrollingMessage("Door Unlocked!"); // adding a message to the side

                        // Removes treasure room doors
                        gp.obj[1] = null;
                        gp.obj[2] = null;
                    }
                    else{
                        // Makes sure that it does not repeatedly play the sound effect and add the scrolling message
                        // Only play sound and add message when the message already fades out
                        while(!gp.uinterface.message.contains("Locked!")){
                            gp.playSFX(1); // Door Locked Sound Effect
                            gp.uinterface.addScrollingMessage("Locked!");
                        }
                    }
                    break;

                case "Chest":
                    gp.obj[i] = null; // Removing the chest from the game
                    gp.gameState = gp.winState; // Changes from playstate to winstate
                    break;
            }
        }
    }

    // Drawing the player
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int playerScreenX = screenX;
        int playerScreenY = screenY;

            // Changing player images depending on the direction they are facing and depending on if they are attacking or not
            switch(direction) {

                case "up":
                    if(!attackState){
                        if(spriteNumber == 1){image = up1;}
                        else if(spriteNumber == 2){image = up2;}
                    }
                    if(attackState){
                        playerScreenY = screenY - gp.tileSize; // Shifting player position due to the taller height of attacking sprite
                        if(spriteNumber == 1){image = attackUp1;}
                        else if(spriteNumber == 2){image = attackUp2;}
                    }
                    break;

                case "down":
                    if(!attackState){
                        if(spriteNumber == 1){image = down1;}
                        else if(spriteNumber == 2){image = down2;}
                    }
                    else{
                        if(spriteNumber == 1){image = attackDown1;}
                        else if(spriteNumber == 2){image = attackDown2;}
                    }

                    break;

                case "left":
                    if(!attackState){
                        if(spriteNumber == 1){image = left1;}
                        else if(spriteNumber == 2){image = left2;}
                    }
                    else{
                        playerScreenX = screenX - gp.tileSize; // Shifting player position due to the longer width of attacking sprite
                        if(spriteNumber == 1){image = attackLeft1;}
                        else if(spriteNumber == 2){image = attackLeft2;}
                    }

                    break;

                case "right":
                    if(!attackState){
                        if(spriteNumber == 1){image = right1;}
                        else if(spriteNumber == 2){image = right2;}
                    }
                    else{
                        if(spriteNumber == 1){image = attackRight1;}
                        else if(spriteNumber == 2){image = attackRight2;}
                    }
                    break;

            }

            // Lowering opacity to show that player is invincible
        if(invincible){
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        g2.drawImage(image, playerScreenX, playerScreenY, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
