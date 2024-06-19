package main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class Sound {
    Clip clip;
    URL soundURL[] = new URL[20];
    FloatControl fc;
    int volumeState = 1;
    float volume;


    public Sound(){

        soundURL[0] = getClass().getResource("/sound/music.wav");
        soundURL[1] = getClass().getResource("/sound/doorlocked.wav");
        soundURL[2] = getClass().getResource("/sound/doorunlocked.wav");
        soundURL[3] = getClass().getResource("/sound/hit.wav");
        soundURL[4] = getClass().getResource("/sound/playerdamaged.wav");
        soundURL[5] = getClass().getResource("/sound/cursor.wav");
        soundURL[6] = getClass().getResource("/sound/victory.wav");
        soundURL[7] = getClass().getResource("/sound/gameover.wav");
        soundURL[8] = getClass().getResource("/sound/keypickedup.wav");
    }

    // Loads an audio file specified by the given index and prepares to play it
    public void setFile(int i){
        try{
            // Obtain an audio input stream from the specified sound URL
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);

            // Obtain a Clip instance for playing the audio
            clip = AudioSystem.getClip();

            // Open the audio clip with the obtained audio input stream
            clip.open(ais);

            // Obtain the float control for adjusting the master gain (volume) of the clip
            fc = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Adjust the volume based on predefined settings (muting and unmuting)
            checkVolume();
        }
        catch(Exception e){}
    }

    // Play a sound
    public void play(){
        clip.start();
    }

    // Loop a sound
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Stop playing a sound
    public void stop(){
        clip.stop();
    }

    // Muting and unmuting sounds
    public void checkVolume(){
        switch(volumeState) {
            case 0:
                // Muting
                volume = -80f;
                break;

            case 1:
                // Unmuting
                volume = 1f;
                break;
        }
        fc.setValue(volume);
    }
}
