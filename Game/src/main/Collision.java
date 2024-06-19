package main;

import entity.Entity;

public class Collision {

    GamePanel gp;
    public Collision(GamePanel gp){
        this.gp = gp;
    }

    // Checking if entity is hitting a solid tile
    public void checkTile(Entity entity){

        // Finding position of the player's solid area (hit-box) with respect to the world map
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Finding column and row numbers of the coordinates
        int entityLeftCol = entityLeftWorldX/gp.tileSize;
        int entityRightCol = entityRightWorldX/gp.tileSize;
        int entityTopRow = entityTopWorldY/gp.tileSize;
        int entityBottomRow = entityBottomWorldY/gp.tileSize;

        // Integer variables to check two tiles for each direction
        int tileNum1, tileNum2;

        // Predict where player will be after moving
        switch(entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow]; // Checking collision on left side
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow]; // Checking collision on right side
                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true;
                }
                break;

            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow]; // Checking collision on left side
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow]; // Checking collision on right side

                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true; // Entity collides with the solid tile and stops moving
                }
                break;

            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow]; // Checking collision on left side
                tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow]; // Checking collision on right side

                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true; // Entity collides with the solid tile and stops moving
                }
                break;

            case "right":
                entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
                tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow]; // Checking collision on left side
                tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow]; // Checking collision on right side

                if(gp.tileM.tile[tileNum1].collision || gp.tileM.tile[tileNum2].collision){
                    entity.collisionOn = true; // Entity collides with the solid tile and stops moving
                }
                break;
        }
    }

    // Checking if entity is hitting an object
    public int checkObject(Entity entity, boolean player){
        int index = 999;

        for(int i = 0;i < gp.obj.length; i++){
            if(gp.obj[i] != null){
                // Entity's solid area (hit-box)
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Object's solid area (hit-box)
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }

                if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                    if(gp.obj[i].collision){
                        entity.collisionOn = true;
                    }
                    if(player){
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    // Collision for other moving entities
    public int checkEntity(Entity entity, Entity[] target){
        int index = 999;

        for(int i = 0;i < target.length; i++){
            if(target[i] != null){
                // Entity's solid area (hit-box)
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Target entity's solid area (hit-box)
                target[i].solidArea.x = target[i].worldX + target[i].solidArea.x;
                target[i].solidArea.y = target[i].worldY + target[i].solidArea.y;

                // Entity's solid area's predicted next position
                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }

                // Checks if the entity comes in contact with another entity
                if(entity.solidArea.intersects(target[i].solidArea)){
                    // If the target is not the entity itself, it will collide
                    if(target[i] != entity){
                        entity.collisionOn = true;
                        index = i;
                    }
                }

                // Reverting the solid area values from the predicted values to the original values
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                target[i].solidArea.x = target[i].solidAreaDefaultX;
                target[i].solidArea.y = target[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    // Checking if entity comes in contact with the player
    public boolean checkPlayer(Entity entity){

        // Boolean for checking if monster is in contact with player (for monster attack feature)
        boolean contactPlayer = false;

        // Entity's solid area (hit-box)
        entity.solidArea.x = entity.worldX + entity.solidArea.x;
        entity.solidArea.y = entity.worldY + entity.solidArea.y;

        // Player's solid area (hit-box)
        gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
        gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;

        // Entity's solid area's predicted next position
        switch(entity.direction){
            case "up":
                entity.solidArea.y -= entity.speed;
                break;
            case "down":
                entity.solidArea.y += entity.speed;
                break;
            case "left":
                entity.solidArea.x -= entity.speed;
                break;
            case "right":
                entity.solidArea.x += entity.speed;
                break;
        }

        // Checks if the entity comes in contact with the player
        if(entity.solidArea.intersects(gp.player.solidArea)){
            entity.collisionOn = true;
            contactPlayer = true;
        }

        // Reverting the solid area values from the predicted values to the original values
        entity.solidArea.x = entity.solidAreaDefaultX;
        entity.solidArea.y = entity.solidAreaDefaultY;
        gp.player.solidArea.x = gp.player.solidAreaDefaultX;
        gp.player.solidArea.y = gp.player.solidAreaDefaultY;

        return contactPlayer;
    }

}
