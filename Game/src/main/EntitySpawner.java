package main;

import mobs.MOB_KeySpider;
import mobs.MOB_Spider;
import object.*;

import java.util.Arrays;
import java.util.Random;

public class EntitySpawner implements RoomLayouts{
    GamePanel gp;
    private Random randomNumberGenerator = new Random();
    boolean[] roomChecked = new boolean[7];
    Boolean[] roomCleared = new Boolean[7];
    int[] doorCounts, keySpiderLocation;
    int[][] keySpiderLocationList;
    private int startX, startY, originalObjectCount, objectCount, treasureCol;

    public EntitySpawner(GamePanel gp){
        this.gp = gp;
        this.doorCounts = new int[]{6, 2, 8, 2, 2, 2, 2}; // How many doors each room has
        this.keySpiderLocationList = new int[][]{{13,32}, {33,22}, {33,32}}; // Possible key spider locations

        // First room top left coordinates
        startX = 14*gp.tileSize;
        startY = 23*gp.tileSize;
    }

    // Default values for if a room is already checked and cleared
    public void setDefaultRoomValues(){
        keySpiderLocationList = new int[][]{{13,32}, {33,22}, {33,32}};
        for (int i = 0; i < roomChecked.length; i++) {
            roomChecked[i] = false;
            roomCleared[i] = false;
        }
    }

    // Adding objects to the game
    public void setObject() {
        // Setting initial object count (excluding the treasure room setting)
        objectCount = 6;

        // TREASURE LOCATION
        int randomTreasureLocation = randomNumberGenerator.nextInt(2) +1;
        switch(randomTreasureLocation){
            case 1:
                setTreasureRoom(23, 8);
                treasureCol = 23;
                break;
            case 2:
                setTreasureRoom(33, 38);
                treasureCol = 33;
                break;
        }

        // Changing the key spider location, so it does not spawn in the treasure room
        if(treasureCol == 33){
            keySpiderLocationList[2] = new int[]{23, 2};
        }

        // SPAWN DECOR OBJECTS
        gp.obj[3] = new Decor_Candle(gp);
        gp.obj[3].worldX = 9 * gp.tileSize;
        gp.obj[3].worldY = 22 * gp.tileSize;

        gp.obj[4] = new Decor_Candle(gp);
        gp.obj[4].worldX = 3 * gp.tileSize;
        gp.obj[4].worldY = 22 * gp.tileSize;

        // LOADING MAP OBJECTS
        roomLayoutRandomizer(); // Room 1
        roomLayoutRandomizer(0, 10); // Room 2
        roomLayoutRandomizer(10, 0); // Room 3
        roomLayoutRandomizer(10, 10); // Room 4
        roomLayoutRandomizer(20, 0); // Room 5
        roomLayoutRandomizer(10, -10); // Room 6
        if(treasureCol == 23){
            roomLayoutRandomizer(20, 10); // Room 7
        }
        else if(treasureCol == 33){
            roomLayoutRandomizer(10, -20); // Room 7
        }

        // KEY SPIDER LOCATION
        setKeySpiderLocation();
    }

    // Adding monsters to the game
    public void setMonster() {

        // See if room 1 is checked and if player enters the room
        if(!roomChecked[0] && gp.player.worldX== 12*gp.tileSize){

            // Spawn spiders
            spawnMonster(0,0);

            originalObjectCount = objectCount; // Keeping the previous object count without the doors

            // Spawn the door objects
            spawnDoor(11, 25, "side");
            spawnDoor(15, 30, "front");
            spawnDoor(21, 25, "side");

            roomChecked[0] = true;
        }

        // See if room 2 is checked and if player enters the room
        else if(!roomChecked[1] && (gp.player.worldX>= 14*gp.tileSize && gp.player.worldX<= 17*gp.tileSize) && gp.player.worldY== 31*gp.tileSize){

            // Spawn spiders
            spawnMonster(0,10);

            originalObjectCount = objectCount; // Keeping the previous object count without the doors
            spawnDoor(15, 30, "front");

            roomChecked[1] = true;
        }

        // See if room 3 is checked and if player enters the room
        else if(!roomChecked[2] && gp.player.worldX== 22*gp.tileSize){

            // Spawn spiders
            spawnMonster(10,0);

            originalObjectCount = objectCount; // Keeping the previous object count without the doors


            spawnDoor(25, 20, "front");
            spawnDoor(25, 30, "front");
            spawnDoor(21, 25, "side");
            spawnDoor(31, 25, "side");

            roomChecked[2] = true;
        }

        // See if room 4 is checked and if player enters the room
        else if(!roomChecked[3] && (gp.player.worldX>= 24*gp.tileSize && gp.player.worldX<= 27*gp.tileSize) && gp.player.worldY== 31*gp.tileSize){

            // Spawn spiders
            spawnMonster(10,10);

            originalObjectCount = objectCount; // Keeping the previous object count without the doors
            spawnDoor(25, 30, "front");

            // Spawn door if one of the adjacent room is not the treasure room
            if(treasureCol != 33){
                doorCounts[3] = 4;
                spawnDoor(31, 35, "side");
            }

            roomChecked[3] = true;
        }

        // See if room 5 is checked and if player enters the room
        else if(!roomChecked[4] && (gp.player.worldY>= 24*gp.tileSize && gp.player.worldY<= 27*gp.tileSize) && gp.player.worldX== 32*gp.tileSize){

            // Spawn spiders
            spawnMonster(20,0);

            originalObjectCount = objectCount; // Keeping the previous object count without the doors
            spawnDoor(31, 25, "side");

            roomChecked[4] = true;
        }

        // See if room 6 is checked and if player enters the room
        else if(!roomChecked[5] && gp.player.worldY== 19*gp.tileSize){

            // Spawn spiders
            spawnMonster(10,-10);
            originalObjectCount = objectCount; // Keeping the previous object count without the doors
            spawnDoor(25, 20, "front");

            // Spawn door if one of the adjacent room is not the treasure room
            if(treasureCol != 23){
                doorCounts[5] = 4;
                spawnDoor(25, 10, "front");
            }

            roomChecked[5] = true;
        }

        // Room 7 (the other possible treasure room)

        //Check if treasure column is 23
        if(treasureCol == 23){

            // See if room 7 is checked and if player enters the other possible treasure room
            if(!roomChecked[6] && (gp.player.worldY>= 34*gp.tileSize && gp.player.worldY<= 37*gp.tileSize) && gp.player.worldX== 32*gp.tileSize){

                // Spawn spiders
                spawnMonster(20,10);
                originalObjectCount = objectCount; // Keeping the previous object count without the doors

                spawnDoor(31, 35, "side");

                roomChecked[6] = true;
            }
        }

        //Check if treasure column is 33
        else if(treasureCol == 33){

            // See if room 7 is checked and if player enters the other possible treasure room
            if(!roomChecked[6] && gp.player.worldY== 9*gp.tileSize){

                // Spawn spiders
                spawnMonster(10,-20);
                originalObjectCount = objectCount;

                spawnDoor(25, 10, "front");

                roomChecked[6] = true;
            }
        }

        // Checking if room is cleared
        for (int i = 0; i < roomCleared.length; i++) {
            if(roomCleared[i] != null && !roomCleared[i]){
                roomCleared[i] = roomClearedCondition(roomChecked[i]); // Checking if all mobs killed
            }
        }

        // Removing doors when all entities in that room are killed (room is cleared)
        for (int i = 0; i < roomCleared.length; i++) {
            if(roomCleared[i] != null && roomCleared[i]){
                removeDoors(roomCleared[i], doorCounts[i]); // Removing doors if room is cleared
                roomCleared[i] = null; // Setting the current room cleared value to null so that it won't be accessed by the loop again
            }

        }
    }

    // Setting the treasure room at the start of the game (there are two options for the game to choose from)
    public void setTreasureRoom(int leftCol, int bottomRow){

        // Coordinates of the chest
        int x = (leftCol*gp.tileSize) + gp.tileSize*3;
        int y = (bottomRow*gp.tileSize) - gp.tileSize*3;

        // Spawning the chest
        gp.obj[0] = new Block_Chest(gp);
        gp.obj[0].worldX = x;
        gp.obj[0].worldY = y;

        // Spawning the doors based on the given column
        if(leftCol == 23){
            gp.obj[1] = new Block_Door(gp, "left", true);
            gp.obj[1].worldX = 25 * gp.tileSize;
            gp.obj[1].worldY = 10 * gp.tileSize;

            gp.obj[2] = new Block_Door(gp, "right", true);
            gp.obj[2].worldX = 26 * gp.tileSize;
            gp.obj[2].worldY = 10 * gp.tileSize;
        }

        else if(leftCol == 33){
            gp.obj[1] = new Block_Door(gp, "up", true);
            gp.obj[1].worldX = 31 * gp.tileSize;
            gp.obj[1].worldY = 35 * gp.tileSize;

            gp.obj[2] = new Block_Door(gp, "down", true);
            gp.obj[2].worldX = 31 * gp.tileSize;
            gp.obj[2].worldY = 36 * gp.tileSize;
        }
    }

    // Spawning monsters when player enters a room
    public void spawnMonster(int xOffset, int yOffset){

        // Original column and row (top left)
        int originalRoomCol = startX -gp.tileSize + xOffset*gp.tileSize;
        int originalRoomRow = startY - gp.tileSize + yOffset*gp.tileSize;

        // Variables that are going to be changed
        int roomRow = originalRoomRow;
        int roomCol = originalRoomCol;

        // Checking if the room is the key spider room
        if(roomCol/gp.tileSize == keySpiderLocation[0] && roomRow/gp.tileSize == keySpiderLocation[1]){

            // Spawn key spider
            gp.monster[0] = new MOB_KeySpider(gp);
            gp.monster[0].worldX = roomCol;
            gp.monster[0].worldY = roomRow;
        }

        else{

            // Spawn normal spider
            gp.monster[0] = new MOB_Spider(gp);
            gp.monster[0].worldX = roomCol;
            gp.monster[0].worldY = roomRow;
        }

        // Move towards the top right corner
        roomCol += gp.tileSize*6;

        // Spawn spider
        gp.monster[1] = new MOB_Spider(gp);
        gp.monster[1].worldX = roomCol;
        gp.monster[1].worldY = roomRow;

        // Move towards the bottom right corner
        roomRow += gp.tileSize*6;

        // Spawn spider
        gp.monster[2] = new MOB_Spider(gp);
        gp.monster[2].worldX = roomCol;
        gp.monster[2].worldY = roomRow;

        // Move towards the bottom left corner
        roomCol = originalRoomCol;

        // Spawn spider
        gp.monster[3] = new MOB_Spider(gp);
        gp.monster[3].worldX = roomCol;
        gp.monster[3].worldY = roomRow;
    }

    // Spawn the doors when player enters a room
    public void spawnDoor(int col, int row, String doorDirection){

        // Side facing doors
        if(doorDirection == "side"){
            int botRow = row + 1;

            gp.obj[objectCount] = new Block_Door(gp, "up", false);
            gp.obj[objectCount].worldX = col * gp.tileSize;
            gp.obj[objectCount].worldY = row * gp.tileSize;

            objectCount++;

            gp.obj[objectCount] = new Block_Door(gp, "down", false);
            gp.obj[objectCount].worldX = col * gp.tileSize;
            gp.obj[objectCount].worldY = botRow * gp.tileSize;

            objectCount++;
        }

        // Front facing doors
        else if(doorDirection == "front"){
            int rightCol = col + 1;

            gp.obj[objectCount] = new Block_Door(gp, "left", false);
            gp.obj[objectCount].worldX = col * gp.tileSize;
            gp.obj[objectCount].worldY = row * gp.tileSize;

            objectCount++;

            gp.obj[objectCount] = new Block_Door(gp, "right", false);
            gp.obj[objectCount].worldX = rightCol * gp.tileSize;
            gp.obj[objectCount].worldY = row * gp.tileSize;

            objectCount++;
        }

    }

    // Check if room is cleared (all mobs are killed)
    public Boolean roomClearedCondition(boolean roomChecked){
        return roomChecked && gp.monster[0] == null && gp.monster[1] == null && gp.monster[2] == null && gp.monster[3] == null;
    }

    // Remove the doors in the room
    public void removeDoors(Boolean roomCleared, int doorCount){
        if(roomCleared){
            for(int i = 1; i <= doorCount; i++){
                gp.obj[objectCount-i] = null;
            }
            objectCount = originalObjectCount;
        }
    }

    // Different Room Layouts (to be assigned by random number generator for each room)
    public void roomLayout(String[] layoutArray, int xOffset, int yOffset){
        // Original column
        int originalRoomCol = startX + xOffset*gp.tileSize;

        // Row and column values that are going to be changed
        int roomRow = startY + yOffset*gp.tileSize;
        int roomCol = originalRoomCol;

        // Total blocks in the middle of the room
        int totalBlocks = 25;

        for(int i = 1; i<totalBlocks+1; i++){

            // Deploy a box object if the array shows Block
            if(layoutArray[i-1] == "Block"){
                gp.obj[objectCount] = new Block_Box(gp);
                gp.obj[objectCount].worldX = roomCol;
                gp.obj[objectCount].worldY = roomRow;
                objectCount++;

                gp.tileM.mapTileNum[roomCol/gp.tileSize][roomRow/gp.tileSize] = 1; // Replacing the tile with a solid tile
            }

            roomCol += gp.tileSize;

            // Move to another row everytime it reaches the fifth column
            if(i%5 == 0){
                roomRow += gp.tileSize;
                roomCol = originalRoomCol;
            }
        }
    }

    // Using random integer generator to choose the room layout
    public void roomLayoutRandomizer(){
        roomLayoutRandomizer(0,0);
    }

    // Randomize the room layouts based on random number generator
    public void roomLayoutRandomizer(int xOffset, int yOffset){
        int randomRoomLayoutValue = randomNumberGenerator.nextInt(100) +1;

        if(randomRoomLayoutValue > 0 && randomRoomLayoutValue <=33){
            roomLayout(styleOne, xOffset, yOffset);
        }
        else if(randomRoomLayoutValue > 33 && randomRoomLayoutValue <=66){
            roomLayout(styleTwo, xOffset, yOffset);
        }
        else if(randomRoomLayoutValue > 66 && randomRoomLayoutValue <=100){
            roomLayout(styleThree, xOffset, yOffset);
        }
    }

    // Setting the random location of the spider
    public void setKeySpiderLocation(){
        int randomSpiderLocation = randomNumberGenerator.nextInt(3); // Random number
        keySpiderLocation = keySpiderLocationList[randomSpiderLocation]; //
    }

}
