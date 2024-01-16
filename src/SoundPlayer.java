import java.io.File;

import javax.sound.sampled.*;

public class SoundPlayer{
    public void playSound(String soundFilePath){

        try {

            // Convert the sound file path into an absolute path
            soundFilePath = new File(soundFilePath).getAbsolutePath();
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(soundFilePath));
            
            try{
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();

            } catch (Exception e){
                e.printStackTrace();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}