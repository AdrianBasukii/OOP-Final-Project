package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.InputStream;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Random;

public class TileManager {
    GamePanel gp;
    public Tile[] tile; // Array of imported tiles
    public int[][] mapTileNum; // Map
    ArrayList<String> tileFileNameList = new ArrayList<>(); // List of filenames from tileData.txt
    ArrayList<String> tileCollisionList = new ArrayList<>(); // List of collision status from tileData.txt
    public boolean drawPath = false; // Drawing the path for pathfinding algorithm

    public TileManager(GamePanel gp){
        this.gp = gp;

        // Reading tile data file (name + solid or not)
        InputStream is = getClass().getResourceAsStream("/maps/tileData.txt");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));

        String line;

        try{
            // Read every line in the tile data file, adding the tile's file name and collision status (solid or not)
            while((line = bReader.readLine()) != null){
                tileFileNameList.add(line);
                tileCollisionList.add(bReader.readLine());
            }
            bReader.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

        // Initializing the tile array based on the files
        tile = new Tile[tileFileNameList.size()];
        getTileImage(); // Setting up the tile

        mapTileNum = new int[gp.maxWorldCol] [gp.maxWorldRow];

        // Loading the map
        loadMap("/maps/map.txt");
    }

    // Getting tile images from resources folder and appending the tile object to tile array
    public void getTileImage() {
        for(int i = 0; i < tileFileNameList.size(); i++){
            String fileName;
            boolean solid;

            fileName = tileFileNameList.get(i); // Gets the filename for the tile's image file
            solid = tileCollisionList.get(i).equals("true"); // Gets and sets collision value for the tile

            setup(i, fileName, solid); // Creates the tile and adds it to the tile list
        }
    }

    // Setting up each new tile
    public void setup(int index, String imageTitle, boolean collision){
        UtilityTool uTool = new UtilityTool(); // Initializing utility tool for scaling images
        try{
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/"+imageTitle));
            tile[index].image = uTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Loading the map by reading the text file
    public void loadMap(String world) {
        try{
            // Get the InputStream for the map txt file
            InputStream is = getClass().getResourceAsStream(world);

            // Wrap the InputStream with InputStreamReader to read characters from bytes
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Top left corner coordinates (start point)
            int col = 0;
            int row = 0;

            // Loop as long as row and column values have not reached the maximum number of columns and rows
            while(col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine();
                String[] numbers = line.split(" "); // Splitting each row into an array

                // Looping until it reaches the max column
                while(col < gp.maxWorldCol){

                    int num = Integer.parseInt(numbers[col]); // Convert number in a column (a string) to an integer

                    mapTileNum[col][row] = num;
                    col++;
                }

                // If it reaches max column, move to the next row
                if(col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        }
        catch(Exception e){}
    }
    public void draw(Graphics2D g2){

        // Start from top left corner of the map
        int worldCol = 0;
        int worldRow = 0;

        // Looping through the map
        while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow){

            // Tile number on the specified location of the map
            int tileNum = mapTileNum[worldCol] [worldRow];

            // Tile's position on the world map
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // Tile's screen position
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // Limiting the tiles rendered to only the tiles that are seen on the screen area
            if(worldX + gp.tileSize> gp.player.worldX - gp.player.screenX && worldX - gp.tileSize< gp.player.worldX + gp.player.screenX &&
               worldY + gp.tileSize > gp.player.worldY - gp.player.screenY && worldY - gp.tileSize < gp.player.worldY + gp.player.screenY){
                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null); // Drawing the tile's image
            }

            worldCol++;

            // Moving to another row
            if(worldCol == gp.maxWorldCol){
                worldCol = 0;
                worldRow++;
            }
        }

        // Drawing the path to show the pathfinding algorithm (for testing purposes)
        if(drawPath){
            g2.setColor(new Color(255,0,0,70));
            for(int i = 0; i < gp.pathFinder.pathList.size();i++){
                int worldX = gp.pathFinder.pathList.get(i).col * gp.tileSize;
                int worldY = gp.pathFinder.pathList.get(i).row * gp.tileSize;
                int screenX = worldX - gp.player.worldX + gp.player.screenX;
                int screenY = worldY - gp.player.worldY + gp.player.screenY;

                g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            }
        }
    }
}
