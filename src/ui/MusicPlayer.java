package ui;

import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {
    private Clip clip;
    private boolean isPlaying = false;

    private static MusicPlayer instance;

    public void play(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 循环播放
            clip.start();
            isPlaying = true;
            instance = this;
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

    public static void stopBgm() {
        if (instance != null && instance.clip != null && instance.clip.isRunning()) {
            instance.clip.stop();
            instance.isPlaying = false;
        }
    }

    public static void playEffect(String path) {

        try {

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(
                            new File(path)
                    );

            Clip clip =
                    AudioSystem.getClip();

            clip.open(audioInputStream);

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

