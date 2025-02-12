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
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class GUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::createGUI);
    }

    private static void createGUI() {   
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Constants.FRAME_SIZE.width, Constants.FRAME_SIZE.height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JFXPanel jfxPanel = new JFXPanel();
        frame.add(jfxPanel, BorderLayout.CENTER);

        Platform.runLater(() -> initFX(jfxPanel, frame));
        frame.setVisible(true);
    }

    private static MediaPlayer createMediaPlayer(String filePath, boolean autoPlay, int cycleCount) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println(filePath + " file not found!");
            return null;
        }
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
        mediaPlayer.setAutoPlay(autoPlay);
        mediaPlayer.setCycleCount(cycleCount);
        return mediaPlayer;
    }

    private static MediaView createMediaView(MediaPlayer mediaPlayer, double width, double height) {
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(width);
        mediaView.setFitHeight(height);
        mediaView.setPreserveRatio(false);
        return mediaView;
    }

    private static void initFX(JFXPanel jfxPanel, JFrame frame) {
        MediaPlayer splashMediaPlayer = createMediaPlayer(Constants.SPLASH_VIDEO_PATH, true, 1);
        if (splashMediaPlayer == null) return;

        splashMediaPlayer.setStopTime(Duration.seconds(1)); // Set splash screen duration to 5 seconds

        MediaView splashMediaView = createMediaView(splashMediaPlayer, 1000, 600);
        StackPane splashPane = new StackPane(splashMediaView);
        jfxPanel.setScene(new Scene(splashPane, Color.BLACK));

        splashMediaPlayer.setOnEndOfMedia(() -> Platform.runLater(() -> fadeToLobby(jfxPanel, frame)));
    }

    private static void fadeToLobby(JFXPanel jfxPanel, JFrame frame) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane lobbyPane = new Pane(lobbyMediaView);

        ImageView imageView = new ImageView(new Image("file:" + Constants.IMAGE_PATH));
        imageView.setFitWidth(700);
        imageView.setPreserveRatio(true);
        imageView.setLayoutX((1000 - imageView.getFitWidth()) / 2);
        imageView.setLayoutY(10);
        lobbyPane.getChildren().add(imageView);

        addButtonsToPane(lobbyPane, jfxPanel, frame);

        jfxPanel.setScene(new Scene(lobbyPane, Color.BLACK));
    }

    private static void showSelectCategoryScreen(JFXPanel jfxPanel, JFrame frame, Pane lobbyPane) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane categoryPane = new Pane(lobbyMediaView);

        addCategoryButtonsToPane(categoryPane, jfxPanel, frame);

        Scene categoryScene = new Scene(categoryPane, 1000, 600);
        
        jfxPanel.setScene(categoryScene);
    }

    private static Button createImageButton(Image image, double x, double y, double width, double height) {
        return createImageButton(image, null, null, x, y, width, height, null);
    }

    private static Button createImageButton(Image image, Image hoverImage, Image clickImage, double x, double y, double width, double height, EventHandler<ActionEvent> action) {
        Button button = new Button();
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(width);
        button.setPrefHeight(height);

        if (hoverImage != null && clickImage != null) {
            button.setOnMouseEntered(event -> setButtonGraphic(button, hoverImage, width, height));
            button.setOnMouseExited(event -> setButtonGraphic(button, image, width, height));
            button.setOnMousePressed(event -> setButtonGraphic(button, clickImage, width, height));
            button.setOnMouseReleased(event -> setButtonGraphic(button, image, width, height));
        }

        if (action != null) {
            button.setOnAction(action);
        }

        return button;
    }

    private static void setButtonGraphic(Button button, Image image, double width, double height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        button.setGraphic(imageView);
    }
 
    private static void addButtonsToPane(Pane pane, JFXPanel jfxPanel, JFrame frame) {
        Image imgStart = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/start.png");
        Image imgStartHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/start-hover.png");
        Image imgStartClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/start-click.png");
        Image imgHowTo = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/how_to.png");
        Image imgHowToHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/how_to-hover.png");
        Image imgHowToClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/how_to-click.png");
        Image imgCredits = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/credits.png");
        Image imgCreditsHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/credits-hover.png");
        Image imgCreditsClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/credits-click.png");
        Image imgExit = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/exit.png");
        Image imgExitHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/exit-hover.png");
        Image imgExitclick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/exit-click.png");
        Image imgMusic = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/music-on.png");
        Image imgSFX = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/sfx-on.png");

        Button buttonStart = createImageButton(imgStart, imgStartHover, imgStartClick, 350, 220, 250, 60, event -> showSelectCategoryScreen(jfxPanel, frame, pane));
        Button buttonHowTo = createImageButton(imgHowTo, imgHowToHover, imgHowToClick, 350, 280, 250, 60, event -> JOptionPane.showMessageDialog(null, "How to play:"));
        Button buttonCredits = createImageButton(imgCredits, imgCreditsHover, imgCreditsClick, 350, 340, 250, 60, event -> JOptionPane.showMessageDialog(null, "Credits:"));
        Button buttonExit = createImageButton(imgExit, imgExitHover, imgExitclick, 350, 400, 250, 60, event -> System.exit(0));
        Button buttonMusic = createImageButton(imgMusic, 870, 500, 30, 30);
        Button buttonSFX = createImageButton(imgSFX, 920, 500, 30, 30);

        pane.getChildren().addAll(buttonStart, buttonHowTo, buttonCredits, buttonExit, buttonMusic, buttonSFX);
    }

    private static void addCategoryButtonsToPane(Pane pane, JFXPanel jfxPanel, JFrame frame) {
        Image imgEasy = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/easy.png");
        Image imgEasyHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/easy-hover.png");
        Image imgEasyClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/easy-click.png");

        Image imgAverage = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/avg.png");
        Image imgAverageHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/avg-hover.png");
        Image imgAverageClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/avg-click.png");

        Image imgDifficult = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/diff.png");
        Image imgDifficultHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/diff-hover.png");
        Image imgDifficultClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/diff-click.png");

        Image imgReturn = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/return.png");
        Image imgReturnHover = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/return-hover.png");
        Image imgReturnClick = new Image("file:/D:/Documents/CMSC-125-Hangover-main/Hangover/resources/return-click.png");


        Button buttonEasy = createImageButton(imgEasy, imgEasyHover, imgEasyClick, 350, 220, 250, 60,   event -> showHangmanScreen());
        Button buttonAverage = createImageButton(imgAverage, imgAverageHover, imgAverageClick, 350, 300, 250, 60,   event -> showHangmanScreen());
        Button buttonDifficult = createImageButton(imgDifficult, imgDifficultHover, imgDifficultClick, 350, 380, 250, 60, event -> showHangmanScreen());
        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30, event -> fadeToLobby(jfxPanel, frame));

        pane.getChildren().addAll(buttonEasy, buttonAverage, buttonDifficult, buttonReturn);
    }

    public static void showHangmanScreen() {
        Hangman hangman = new Hangman();
        hangman.setVisible(true);
    }    
}