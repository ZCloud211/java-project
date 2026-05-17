package ui;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {
    private Clip clip;
    private boolean isPlaying = false;

    public void play(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 循环播放
            clip.start();
            isPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggle() {
        if (isPlaying) {
            clip.stop();
            isPlaying = false;
        } else {
            clip.start();
            isPlaying = true;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
