import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GUI {
    private static final String SPLASH_VIDEO_PATH = "C:\\Users\\Eugene\\Desktop\\125\\Hangover\\src\\Loading Screen.mp4"; // Change this to your splash screen video file path
    private static final String LOBBY_VIDEO_PATH = "C:\\Users\\Eugene\\Desktop\\125\\Hangover\\src\\Background_Lobby.mp4"; // Change this to your lobby video file path
    private static final String IMAGE_PATH = "C:\\Users\\Eugene\\Desktop\\125\\Hangover\\src\\Title.png"; // Change this to your image file path
    private static final int SPLASH_SCREEN_DURATION = 5000; // 5 seconds
    private static final int FADE_DURATION = 3000; // 3 seconds

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::createGUI);
    }

    private static void createGUI() {
        JFrame frame = new JFrame("Video Background GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // This centers the frame

        // Use a BorderLayout with the video in the center
        frame.setLayout(new BorderLayout());

        // Create the JFXPanel for video rendering
        JFXPanel jfxPanel = new JFXPanel(); // JavaFX Panel inside Swing
        frame.add(jfxPanel, BorderLayout.CENTER);

        // Initialize JavaFX thread
        Platform.runLater(() -> initFX(jfxPanel, frame));

        // Make the frame visible
        frame.setVisible(true);
    }

    private static void initFX(JFXPanel jfxPanel, JFrame frame) {
        // Load splash screen video
        File splashFile = new File(SPLASH_VIDEO_PATH);
        if (!splashFile.exists()) {
            System.out.println("Splash screen video file not found!");
            return;
        }

        Media splashMedia = new Media(splashFile.toURI().toString());
        MediaPlayer splashMediaPlayer = new MediaPlayer(splashMedia);
        splashMediaPlayer.setAutoPlay(true);

        MediaView splashMediaView = new MediaView(splashMediaPlayer);

        // Resize the video to fit 1000x600 dimensions (no aspect ratio)
        splashMediaView.setFitWidth(1000);
        splashMediaView.setFitHeight(600);
        splashMediaView.setPreserveRatio(false); // Ignore aspect ratio to fully fit the frame

        StackPane splashPane = new StackPane(splashMediaView);
        Scene splashScene = new Scene(splashPane, Color.BLACK);
        jfxPanel.setScene(splashScene);

        // Play splash screen video and fade out after 5 seconds
        splashMediaPlayer.setOnEndOfMedia(() -> {
            Platform.runLater(() -> {
                // Fade out to black after splash screen video ends
                FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_DURATION), splashPane);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(event -> fadeToLobby(jfxPanel, frame));
                fadeOut.play();
            });
        });
    }

    private static void fadeToLobby(JFXPanel jfxPanel, JFrame frame) {
        // Load lobby video
        File lobbyFile = new File(LOBBY_VIDEO_PATH);
        if (!lobbyFile.exists()) {
            System.out.println("Lobby video file not found!");
            return;
        }

        Media lobbyMedia = new Media(lobbyFile.toURI().toString());
        MediaPlayer lobbyMediaPlayer = new MediaPlayer(lobbyMedia);
        lobbyMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the lobby video indefinitely
        lobbyMediaPlayer.setAutoPlay(true);

        MediaView lobbyMediaView = new MediaView(lobbyMediaPlayer);

        // Resize the video to fit 1000x600 dimensions (no aspect ratio)
        lobbyMediaView.setFitWidth(1000);
        lobbyMediaView.setFitHeight(600);
        lobbyMediaView.setPreserveRatio(false); // Ignore aspect ratio to fully fit the frame

        // Create a Pane for absolute positioning
        Pane lobbyPane = new Pane();
        lobbyPane.getChildren().add(lobbyMediaView);

        // Create an ImageView for the top middle image
        Image image = new Image("file:" + IMAGE_PATH); // Load image from the file path
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(700); // Adjust the width of the image
        imageView.setPreserveRatio(true); // Maintain the aspect ratio of the image
        imageView.setLayoutX((1000 - imageView.getFitWidth()) / 2); // Center horizontally at the top
        imageView.setLayoutY(10); // Adjust vertical position (top of the frame)

        // Add the image to the lobby pane
        lobbyPane.getChildren().add(imageView);

        // Create three buttons in the middle
        Button button1 = new Button("Option 1");
        Button button2 = new Button("Option 2");
        Button button3 = new Button("Option 3");

        // Set positions of buttons with absolute positioning
        button1.setLayoutX(400); // Horizontal position
        button1.setLayoutY(250); // Vertical position

        button2.setLayoutX(400); // Horizontal position
        button2.setLayoutY(300); // Vertical position

        button3.setLayoutX(400); // Horizontal position
        button3.setLayoutY(350); // Vertical position

        // Add buttons to the pane
        lobbyPane.getChildren().addAll(button1, button2, button3);

        // Create and set the scene for the lobby
        Scene lobbyScene = new Scene(lobbyPane, Color.BLACK); // Lobby background color
        jfxPanel.setScene(lobbyScene);

        // Fade-in the lobby video after fading to black
        Platform.runLater(() -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_DURATION), lobbyPane);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }
}
