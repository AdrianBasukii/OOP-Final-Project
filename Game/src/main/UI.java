package main;

import entity.Entity;
import object.Item_Key;
import object.UI_Heart;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font customFont;

    // Measuring Play Time
    private double playTime; // The play time
    DecimalFormat dFormat = new DecimalFormat("#0.0"); // Rounds the playtime to 1 decimal place

    // Command number to switch between several options in title screen, pause screen, and end screen (win/lose)
    public int commandNum = 0; // Command number
    public int optionsCommandNum = 0; // Command number for pause screen

    // Heart Image
    private final BufferedImage heart_full, heart_half, heart_blank, keyImage;

    // ArrayList to store messages
    public ArrayList<String> message = new ArrayList<>();
    ArrayList<Integer> messageCounter = new ArrayList<>();


    public UI(GamePanel gp) {
        this.gp = gp;

        //KEY ICON
        Entity key = new Item_Key(gp);
        keyImage = key.down1;

        //HEALTH
        Entity heart = new UI_Heart(gp);
        heart_full = heart.image;
        heart_half = heart.image2;
        heart_blank = heart.image3;

        //CUSTOM FONT
        InputStream iStream = getClass().getResourceAsStream("/font/high_pixel-7.ttf");
        try{
            customFont = Font.createFont(Font.TRUETYPE_FONT, iStream);
        }
        catch(FontFormatException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    // Adding a new scrolling message to the list
    public void addScrollingMessage(String text){
        message.add(text);
        messageCounter.add(0);
    }

    // Main draw method that draws different elements based on the game state
    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(customFont);
        g2.setColor(Color.white);

        if(gp.gameState == gp.playState){
            drawPlayerLife(); // Displaying Health
            drawScrollingMessage(); // Scrolling messages
            drawKey(); // Key Obtained Indicator
            playTime += (double) 1/60; // Adding play time
            g2.drawString("Time: "+dFormat.format(playTime), gp.tileSize*11, 65); // Displaying play time
        }
        else if(gp.gameState == gp.pauseState){
            drawPlayerLife(); // Displaying Health
            drawPauseScreen(); // Displaying Pause Screen
        }
        else if(gp.gameState == gp.titleState){
            resetTimer(); // Make sure timer is at 0
            drawTitleScreen(); // Displaying Main Menu
        }
        else if(gp.gameState == gp.gameOverState){
            drawGameOverScreen(); // Displaying Game Over Screen
        }
        else if(gp.gameState == gp.winState){
            drawWinScreen(); // Displaying Win Screen
        }

        g2.dispose();
    }

    // Drawing the key at the top right to indicate that the player obtained the key
    public void drawKey(){

        // If statement makes sure that the key is only drawn when player obtains key
        if(gp.player.keyObtained){
            g2.drawImage(keyImage, gp.screenWidth - gp.tileSize*3, gp.tileSize/2, gp.tileSize*2, gp.tileSize*2, null);
        }

    }

    // Drawing the hearts at the top left of the screen
    public void drawPlayerLife(){

        // Initial heart coordinates
        int heartX = gp.tileSize/2;
        int heartY = gp.tileSize/2;

        int i = 0;

        // Draw blank hearts up to the maximum life (each heart represents 2 life points)
        while(i < gp.player.maxLife/2){

            // Draw a blank heart
            g2.drawImage(heart_blank, heartX, heartY, null);
            i++;

            // Move to the next position to the right
            heartX += gp.tileSize;
        }

        // Reset heart coordinates for drawing filled hearts
        heartX = gp.tileSize/2;
        i = 0;

        // Draw filled hearts up to the current life
        while(i < gp.player.life){

            // Draw a half heart
            g2.drawImage(heart_half, heartX, heartY, null);
            i++;

            // Draw a full heart if the player has more life
            if(i<gp.player.life){
                g2.drawImage(heart_full, heartX, heartY, null);
            }
            i++;

            // Move to the next position to the right
            heartX += gp.tileSize;
        }
    }

    // Side scrolling messages that tells whenever a player does something notable (e.g. killing a mob, picking up the key, or unlocking the treasure room door)
    public void drawScrollingMessage(){

        // Initial message display coordinates
        int messageX = gp.tileSize;
        int messageY = gp.tileSize*3;

        // Font settings
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 24F));
        for(int i = 0; i < message.size(); i++){
            if(message.get(i) != null){

                //SHADING
                g2.setColor(Color.BLACK);
                g2.drawString(message.get(i), messageX+2, messageY+2);

                //TEXT MESSAGE
                g2.setColor(Color.WHITE);
                g2.drawString(message.get(i), messageX, messageY);

                // Increasing counter number by 1 and set position of the next message to be below the previous
                int counter = messageCounter.get(i) + 1;
                messageCounter.set(i, counter);
                messageY += 50;

                // 3 seconds timer before removing text
                if(messageCounter.get(i) > 180){
                    message.remove(i);
                    messageCounter.remove(i);
                }
            }
        }
    }

    // Game over screen: Displayed when player dies
    public void drawGameOverScreen(){

        // Darken background
        g2.setColor(new Color(0,0,0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // X and Y variables for the coordinates of the text and other elements
        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,110F));

        // Shading
        text = "YOU DIED!";
        g2.setColor(Color.black);
        x = centerText(text);
        y = gp.tileSize*4;
        g2.drawString(text, x, y);

        // Game Over Text
        g2.setColor(Color.white);
        g2.drawString(text, x-4, y-4);

        // Retry
        text = "Retry";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,50F));
        x = centerText(text);
        y += gp.tileSize*6;
        g2.drawString(text, x, y);
        if(commandNum == 0){
            g2.drawString(">", x-gp.tileSize, y);
        }

        // Return to main menu (title screen)
        text = "Exit";
        x = centerText(text);
        y += gp.tileSize*2;
        g2.drawString(text, x, y);
        if(commandNum == 1){
            g2.drawString(">", x-gp.tileSize, y);
        }

    }

    // Win screen: Displayed when player picks up treasure
    public void drawWinScreen(){

        // Darken background
        g2.setColor(new Color(0,0,0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // X and Y variables for the coordinates of the text and other elements
        int x;
        int y;
        String text;
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,110F));

        // Shading
        text = "YOU WON!";
        g2.setColor(Color.black);
        x = centerText(text);
        y = gp.tileSize*4;
        g2.drawString(text, x, y);

        // Win Text
        g2.setColor(Color.white);
        g2.drawString(text, x-4, y-4);

        // Time
        g2.setColor(Color.YELLOW);

        text = "Time: " + dFormat.format(playTime);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,70F));
        x = centerText(text);
        y += gp.tileSize*3;
        g2.drawString(text, x, y);


        // Retry
        g2.setColor(Color.white);
        text = "Retry";
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,50F));
        x = centerText(text);
        y += gp.tileSize*3;
        g2.drawString(text, x, y);
        if(commandNum == 0){
            g2.drawString(">", x-gp.tileSize, y);
        }

        // Return to main menu (title screen)
        text = "Exit";
        x = centerText(text);
        y += gp.tileSize*2;
        g2.drawString(text, x, y);
        if(commandNum == 1){
            g2.drawString(">", x-gp.tileSize, y);
        }

    }

    // Drawing the pause screen along with the list of settings
    public void drawPauseScreen(){

        // Setting the text properties
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(28F));

        // Setting up Popup window
        int width = gp.tileSize*10;
        int height = gp.tileSize*12;
        int windowX = gp.screenWidth/2 - width/2;
        int windowY = gp.screenHeight/2 - height/2;
        drawPopup(windowX, windowY, width, height);

        // Displaying the settings
        settingList(windowX, windowY);
    }

    // List of settings to be displayed in pause screen
    public void settingList(int windowX, int windowY){
        int x;
        int y;

        // Title
        String text = "Settings";
        x = centerText(text);
        y = windowY + gp.tileSize;
        g2.drawString(text, x, y);

        // MUSIC SETTING
        text = "Music";
        x = windowX + gp.tileSize;
        y += gp.tileSize*2;
        g2.drawString(text, x, y);
        if(optionsCommandNum == 0){
            g2.drawString(">", x-25, y);
        }

        // SOUND EFFECT SETTING
        text = "Sound Effect";
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(optionsCommandNum == 1){
            g2.drawString(">", x-25, y);
        }

        // RETRY GAME
        text = "Retry Game";
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(optionsCommandNum == 2){
            g2.drawString(">", x-25, y);
        }

        // TITLE SCREEN
        text = "Return to Main Menu";
        y += gp.tileSize;
        g2.drawString(text, x, y);
        if(optionsCommandNum == 3){
            g2.drawString(">", x-25, y);
        }

        // BACK
        text = "Back";
        y += gp.tileSize*4;
        g2.drawString(text, x, y);
        if(optionsCommandNum == 4){
            g2.drawString(">", x-25, y);
        }

        // MUSIC CHECK BOX
        x = windowX + gp.tileSize*7 + 24;
        y = windowY + gp.tileSize*2 + 24;
        g2.drawRect(x, y, 24, 24);
        if(gp.keyH.musicVolCounter == 0){
            g2.fillRect(x, y, 24, 24);
        }

        // SOUND EFFECTS CHECK BOX
        y += gp.tileSize;
        g2.drawRect(x, y, 24, 24);
        if(gp.keyH.soundVolCounter == 0){
            g2.fillRect(x, y, 24, 24);
        }
    }

    public void drawPopup(int x, int y, int width, int height){

        // Main popup body
        Color color = new Color(0, 0, 0, 200);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        // White Border
        color = new Color(255,255,255);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public void drawTitleScreen(){
        // Game Title
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,84F));
        String text = "LITTLE ADVENTURES";
        int x = centerText(text);
        int y = gp.tileSize*3;

        // Text shadow
        g2.setColor(Color.darkGray);
        g2.drawString(text, x+5, y+5);

        // Text color
        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // Display Character
        x = gp.screenWidth/2 - gp.tileSize;
        y += gp.tileSize*2;
        g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

        // Menu
        g2.setFont(g2.getFont().deriveFont(Font.BOLD,48F));

        // Proceed to playing the game
        text = "Start Game";
        x = centerText(text);
        y += gp.tileSize*5;
        g2.drawString(text, x, y);
        if(commandNum == 0){
            g2.drawString(">", x-gp.tileSize, y);
        }

        // Exit Game
        text = "Exit";
        x = centerText(text);
        y += gp.tileSize*2;
        g2.drawString(text, x, y);
        if(commandNum == 1){
            g2.drawString(">", x-gp.tileSize, y);
        }
    }

    // Method that returns the X coordinate that could center a given text
    private int centerText(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth/2 - length/2;
    }

    // Resetting the in game timer
    public void resetTimer(){
        playTime = 0;
    }
}
