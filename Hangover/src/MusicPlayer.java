import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class MusicPlayer extends GUI {
    private MediaPlayer backgroundPlayer;

    // Method for playing background music
    public void playMusic(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }

            Media media = new Media(file.toURI().toString());
            backgroundPlayer = new MediaPlayer(media);
            backgroundPlayer.setOnReady(() -> System.out.println("Music ready to play!"));
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            backgroundPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.play();
        }
    }

    public void stopMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
        }
    }

    public void setVolume(double volume) {
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(volume); // Range: 0.0 to 1.0
        }
    }

    public void seekTo(double seconds) {
        if (backgroundPlayer != null) {
            backgroundPlayer.seek(Duration.seconds(seconds));
        }
    }

    // New method for playing short sound effects
    public void playSoundEffect(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Sound effect not found: " + filePath);
                return;
            }

            Media media = new Media(file.toURI().toString());
            MediaPlayer soundPlayer = new MediaPlayer(media);
            soundPlayer.setVolume(1.0); // Max volume
            soundPlayer.play();

            // Dispose of the sound player when finished
            soundPlayer.setOnEndOfMedia(() -> soundPlayer.dispose());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
