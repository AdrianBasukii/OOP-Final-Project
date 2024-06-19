package main;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import entity.Entity;
import entity.Player;
import object.Item_Key;
import pathfinding.Pathfinder;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
    // Window Size Configs
    final int originalTileSize = 16; // 16x16 size of each tile
    final int scale = 3; // Pixel Scaling based on screen size

    public final int tileSize = originalTileSize * scale; // Scaling up the tile size to match different screen size
    public final int maxScreenCol = 24; // Screen Column
    public final int maxScreenRow = 16; // Screen Row
    public final int screenWidth = tileSize*maxScreenCol; // Window Width
    public final int screenHeight = tileSize*maxScreenRow; // Window Height

    // World Setting
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    // FPS Setting
    int FPS = 60;

    // SYSTEM
    public KeyHandler keyH = new KeyHandler(this);
    Thread gameThread;
    public UI uinterface = new UI(this);
    Sound sound = new Sound(); // Sound Effects
    Sound music = new Sound(); // Background Music
    public TileManager tileM = new TileManager(this); // Map loader
    public Collision collisionChecker = new Collision(this); // Collision checker
    public EntitySpawner entitySpawner = new EntitySpawner(this); // Spawner for objects and monsters (RNG room layouts)
    public Pathfinder pathFinder = new Pathfinder(this); // Mob's pathfinding

    // Game states
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;
    public final int winState = 4;

    // Boolean for checking if victory sound is played to prevent it being played multiple times in the update loop
    private boolean victorySoundPlayed = false;

    // Entity and Objects
    public Player player = new Player(this, keyH);
    public Entity obj[] = new Entity [100]; // Prepare 100 slots for objects
    public Entity monster[] = new Entity [20]; // Prepare 20 slots for monsters
    ArrayList<Entity> entityList = new ArrayList<>(); // List of entities (for arranging their order)
    public ArrayList<Entity> projectileList = new ArrayList<>(); // List of projectiles


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Setting dimensions of window
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH); // Setting keyHandler as the key listener
        this.setFocusable(true); // Allowing window to gain focus when requested
    }

    // Loading initial game states
    public void setupGame(){
        entitySpawner.setObject(); // Placing objects
        entitySpawner.setDefaultRoomValues(); // Setup default room values (unchecked for everything)
        gameState = titleState; // Display title screen
    }

    // Reverting values back to default when retrying
    public void retry(){
        victorySoundPlayed = false; // Make the victorySoundPlayed boolean false
        uinterface.resetTimer();
        player.setDefaultValues(); // Default values (location, health, etc.)
        tileM.loadMap("/maps/map.txt"); // Reloading map
        entitySpawner.setDefaultRoomValues(); // Uncheck every room
        Arrays.fill(monster, null); // Emptying the monster array
        Arrays.fill(obj, null); // Emptying object array
        projectileList.clear(); // Removing every leftover projectile
        entitySpawner.setObject(); // Placing objects
    }

    // Initiate a new game loop, executing the run method
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    // Overriding the main logic of the game loop to limit the game to 60FPS using delta time
    @Override
    public void run(){
        double drawInterval = 1000000000/FPS; // Time interval between updates
        double delta = 0; // Delta value
        long lastTime = System.nanoTime(); // Timestamp when frame starts
        long currentTime;

        // Updating and drawing the game information based on FPS
        while(gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if(delta >= 1){
                // Call update method to update information
                update();

                // Call paintComponent method to redraw
                repaint();

                delta--;
            }
        }
    }

    // Updating game information
    public void update(){
        if(gameState == playState){
            // Player
            player.update();
            entitySpawner.setMonster();

            // Monster
            for(int i = 0; i < monster.length; i++){
                if(monster[i] != null){
                    if(monster[i].alive && !monster[i].dying){
                        monster[i].update();
                    }
                    else if(!monster[i].alive){
                        if(monster[i].name == "KeySpider"){
                            monster[i].dropItem(new Item_Key(this));
                        }
                        monster[i] = null;
                    }
                }
            }

            // Projectiles
            for(int i = 0; i < projectileList.size(); i++){
                if(projectileList.get(i) != null){
                    if(projectileList.get(i).alive){
                        projectileList.get(i).update();
                    }
                    else {
                        projectileList.remove(i);
                    }
                }
            }
        }

        // Stop the music and play victory sound when player wins
        else if(gameState == winState){
            stopMusic();
            if(!victorySoundPlayed){
                playSFX(6);
                victorySoundPlayed = true;
            }
        }


    }

    // Displaying the components
    public void paintComponent(Graphics g){
        super.paintComponent(g); // Paint component receives Graphics object as argument which is the base class for all graphic contexts

        Graphics2D g2 = (Graphics2D)g; // Casting graphics to graphics2d object for enhanced functionality

        // Title Screen
        if(gameState == titleState){
            uinterface.draw(g2);
        }

        // Game
        else{
            // Drawing tiles
            tileM.draw(g2);


            //////////////////////////////////////////////////////////////////////////////////////
            // Entity drawing order settings ////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////


            // Adding Player
            entityList.add(player);

            // Adding Objects
            for (Entity object : obj) {
                if (object != null) {
                    entityList.add(object);
                }
            }

            // Adding Mobs
            for (Entity mob : monster) {
                if (mob != null) {
                    entityList.add(mob);
                }
            }

            // Adding projectiles
            for (Entity projectile : projectileList){
                if(projectile != null){
                    entityList.add(projectile);
                }
            }

            // Sorting entity list by world Y
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    return Integer.compare(e1.worldY, e2.worldY);
                }
            });

            // Drawing Entities
            for (int i = 0; i<entityList.size(); i++) {
               entityList.get(i).draw(g2);
            }

            // Emptying entity list
            entityList.clear();

            // Drawing the UI
            uinterface.draw(g2);
        }

        g2.dispose();
    }

    // Playing the background music
    public void playMusic(int i){
        music.setFile(i);
        music.play();
        music.loop();
    }

    // Stopping the background music (used when player dies or wins)
    public void stopMusic(){
        music.stop();
    }

    // Plays sound effects (hitting monsters, receiving damage, etc.)
    public void playSFX(int i){
        sound.setFile(i);
        sound.play();
    }
}
