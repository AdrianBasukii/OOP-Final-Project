package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    // Boolean checking if movement and attack keys are pressed
    public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
    GamePanel gp;

    // Toggle variables
    private int pCounter = 0; // For toggling drawing the path of mob to player
    int musicVolCounter = 0; // For toggling muting or unmuting music
    int soundVolCounter = 0; // For toggling muting or unmuting sound effects

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //Conditions for keydown
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // Code of the key pressed

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        ///// TITLE STATE ////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        if(gp.gameState == gp.titleState){

            // Move cursor up
            if(code == KeyEvent.VK_UP){
                gp.playSFX(5);
                if(gp.uinterface.commandNum>0){
                    gp.uinterface.commandNum--;
                }
            }

            // Move cursor down
            else if(code == KeyEvent.VK_DOWN){
                gp.playSFX(5);
                if(gp.uinterface.commandNum<1){
                    gp.uinterface.commandNum++;
                }
            }

            // Select option
            else if(code == KeyEvent.VK_ENTER){
                switch(gp.uinterface.commandNum){
                    case 0:
                        gp.gameState = gp.playState;
                        gp.playMusic(0);
                        break;
                    case 1:
                        System.exit(0);
                        break;
                }
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///// PLAY STATE //////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        else if(gp.gameState == gp.playState){

            // Movement keys
            if(code == KeyEvent.VK_W){
                upPressed = true;
            }
            else if(code == KeyEvent.VK_A){
                leftPressed = true;
            }
            else if(code == KeyEvent.VK_S){
                downPressed = true;
            }
            else if(code == KeyEvent.VK_D){
                rightPressed = true;
            }

            // Pause key
            else if(code == KeyEvent.VK_ESCAPE){
                gp.gameState = gp.pauseState;
            }

            // Draw mob's path to player
            else if(code == KeyEvent.VK_P){

                // Toggled using the pCounter variable
                if(pCounter == 0){
                    gp.tileM.drawPath = true;
                    pCounter++;
                }
                else if(pCounter > 0){
                    gp.tileM.drawPath = false;
                    pCounter--;
                }
            }

            // Player attack
            else if(code == KeyEvent.VK_SPACE){
                spacePressed = true;
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///// PAUSE STATE /////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////

        else if(gp.gameState == gp.pauseState){

            // Return to game
            if(code == KeyEvent.VK_ESCAPE){
                gp.gameState = gp.playState;
            }

            // Move cursor up
            else if(code == KeyEvent.VK_UP){
                gp.playSFX(5);
                if(gp.uinterface.optionsCommandNum>0){
                    gp.uinterface.optionsCommandNum--;
                }
            }

            // Move cursor down
            else if(code == KeyEvent.VK_DOWN){
                gp.playSFX(5);
                if(gp.uinterface.optionsCommandNum<4){
                    gp.uinterface.optionsCommandNum++;
                }
            }

            // Selecting options
            else if(code == KeyEvent.VK_ENTER){
                switch(gp.uinterface.optionsCommandNum){
                    case 0:

                        // Muting music
                        if(musicVolCounter == 0){
                            gp.music.volumeState--;
                            musicVolCounter++;
                        }
                        else{
                            gp.music.volumeState++;
                            musicVolCounter--;
                        }
                        gp.music.checkVolume();
                        gp.playSFX(5);

                        break;

                    case 1:

                        // Muting Sound Effects
                        if(soundVolCounter == 0){
                            gp.sound.volumeState--;
                            soundVolCounter++;
                        }
                        else{
                            gp.sound.volumeState++;
                            soundVolCounter--;
                        }
                        gp.playSFX(5);
                        break;

                    case 2:

                        // Retry game
                        gp.gameState = gp.playState;
                        gp.retry(); // Revert to default values
                        break;

                    case 3:

                        // Return to main menu
                        gp.music.stop();
                        gp.gameState = gp.titleState;
                        gp.retry();
                        break;

                    case 4:

                        // Return to game
                        gp.gameState = gp.playState;
                        break;

                }
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        ///// END STATE (win/lose) ///////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        else if(gp.gameState == gp.gameOverState || gp.gameState == gp.winState){

            // Move cursor up
            if(code == KeyEvent.VK_UP){
                gp.playSFX(5);
                if(gp.uinterface.commandNum>0){
                    gp.uinterface.commandNum--;
                }
            }

            // Move cursor down
            else if(code == KeyEvent.VK_DOWN){
                gp.playSFX(5);
                if(gp.uinterface.commandNum<1){
                    gp.uinterface.commandNum++;
                }
            }

            // Select option
            else if(code == KeyEvent.VK_ENTER){
                switch(gp.uinterface.commandNum){
                    case 0:

                        // Retry game
                        gp.gameState = gp.playState;
                        gp.retry(); // Revert to default values
                        gp.playMusic(0);
                        if(musicVolCounter == 1){
                            gp.music.volumeState = 0;
                            gp.music.checkVolume();
                        }
                        break;

                    case 1:

                        // Return to main menu
                        gp.gameState = gp.titleState;
                        gp.retry();
                        break;

                }
            }
        }


    }

    // Conditions for key up
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // Code of the key pressed

        // Stopping player movement
        if(code == KeyEvent.VK_W){
            upPressed = false;
        }
        else if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
        else if(code == KeyEvent.VK_S){
            downPressed = false;
        }
        else if(code == KeyEvent.VK_D){
            rightPressed = false;
        }

        // Attack
        else if(code == KeyEvent.VK_SPACE){
            spacePressed = false;
        }

    }

}
