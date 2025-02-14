// import javafx.animation.FadeTransition;
import javafx.application.Application;
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
import javafx.stage.Stage;
// import javafx.stage.StageStyle;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.File;

// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;

public class GUI extends Application {

    // private MediaPlayer soundmain = createMediaPlayer("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Music.mp3", true, MediaPlayer.INDEFINITE);
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hangman Game");
        primaryStage.setWidth(Constants.FRAME_SIZE.width);
        primaryStage.setHeight(Constants.FRAME_SIZE.height);
        primaryStage.setResizable(false);
        // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        JFXPanel jfxPanel = new JFXPanel();
        Platform.runLater(() -> initFX(jfxPanel, primaryStage));
    }

    // Method to create a MediaPlayer
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

    // Method to create a MediaView
    private static MediaView createMediaView(MediaPlayer mediaPlayer, double width, double height) {
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setFitWidth(width);
        mediaView.setFitHeight(height);
        mediaView.setPreserveRatio(false);
        return mediaView;
    }

    // Method to initialize JavaFX components
    private static void initFX(JFXPanel jfxPanel, Stage primaryStage) {
        MediaPlayer splashMediaPlayer = createMediaPlayer(Constants.SPLASH_VIDEO_PATH, true, 1);
        if (splashMediaPlayer == null) return;

        splashMediaPlayer.setStopTime(Duration.seconds(4)); // Set splash screen duration to 2 seconds

        MediaView splashMediaView = createMediaView(splashMediaPlayer, 1000, 600);
        StackPane splashPane = new StackPane(splashMediaView);
        Scene splashScene = new Scene(splashPane, Color.BLACK);
        primaryStage.setScene(splashScene);
        primaryStage.show();

        splashMediaPlayer.setOnEndOfMedia(() -> Platform.runLater(() -> fadeToLobby(primaryStage)));
    }

    // Method to transition to the lobby screen
    private static void fadeToLobby(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane lobbyPane = new Pane(lobbyMediaView);

        ImageView imageView = createImageView("file:" + Constants.IMAGE_PATH, 700, 0, true, (1000 - 700) / 2, 10);
        lobbyPane.getChildren().add(imageView);

        addButtonsToPane(lobbyPane, primaryStage);

        Scene lobbyScene = new Scene(lobbyPane, Color.BLACK);
        primaryStage.setScene(lobbyScene);
    }

    // Method to show the category selection screen
    private static void showSelectCategoryScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        

        Image selectDifficultyImage = new Image("file:D:/125 Hangman/CMSC-125-Hangover/Hangover/resources/SelecctDifficulty.png");
        ImageView imageView = new ImageView(selectDifficultyImage);
    
        // Ensure image fits without distortion
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600); // Adjust width as needed
        imageView.setLayoutX((1000-600)/2);
        imageView.setLayoutY(50);

        Pane categoryPane = new Pane(lobbyMediaView, imageView);

        addCategoryButtonsToPane(categoryPane, primaryStage);

        Scene categoryScene = new Scene(categoryPane, 1000, 600);
        primaryStage.setScene(categoryScene);
    }

    // Method to show the "How to Play" screen
    private static void showHowToPlayScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane howToPane = new Pane(lobbyMediaView);

        ImageView howToPlayImageView = createImageView(Constants.IMG_HOW_TO_PLAY, 800, 550, true, (1000 - 800) / 2, (600 - 550) / 2);
        howToPane.getChildren().add(howToPlayImageView);

        Image imgReturn = new Image(Constants.IMG_RETURN);
        Image imgReturnHover = new Image(Constants.IMG_RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.IMG_RETURN_CLICK);

        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30, event -> fadeToLobby(primaryStage));
        howToPane.getChildren().add(buttonReturn);

        Scene howToScene = new Scene(howToPane, 1000, 600);
        primaryStage.setScene(howToScene);
    }

    // Method to show the credits screen
    private static void showCreditsScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null) return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane creditsPane = new Pane(lobbyMediaView);

        ImageView creditsImageView = createImageView(Constants.IMG_CREDITS_SCREEN, 800, 550, true, (1000 - 800) / 2, (600 - 550) / 2);
        creditsPane.getChildren().add(creditsImageView);

        Image imgReturn = new Image(Constants.IMG_RETURN);
        Image imgReturnHover = new Image(Constants.IMG_RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.IMG_RETURN_CLICK);

        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30, event -> fadeToLobby(primaryStage));
        creditsPane.getChildren().add(buttonReturn);

        Scene creditsScene = new Scene(creditsPane, 1000, 600);
        primaryStage.setScene(creditsScene);
    }

    // Method to create an image button
    private static Button createImageButton(Image image, double x, double y, double width, double height) {
        return createImageButton(image, null, null, x, y, width, height, null);
    }

    // Overloaded method to create an image button with hover and click effects
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

    // Method to set the graphic of a button
    private static void setButtonGraphic(Button button, Image image, double width, double height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        button.setGraphic(imageView);
    }

    // Method to add buttons to the lobby pane
    private static void addButtonsToPane(Pane pane, Stage primaryStage) {
        Image imgStart = new Image(Constants.IMG_START);
        Image imgStartHover = new Image(Constants.IMG_START_HOVER);
        Image imgStartClick = new Image(Constants.IMG_START_CLICK);
        Image imgHowTo = new Image(Constants.IMG_HOW_TO);
        Image imgHowToHover = new Image(Constants.IMG_HOW_TO_HOVER);
        Image imgHowToClick = new Image(Constants.IMG_HOW_TO_CLICK);
        Image imgCredits = new Image(Constants.IMG_CREDITS);
        Image imgCreditsHover = new Image(Constants.IMG_CREDITS_HOVER);
        Image imgCreditsClick = new Image(Constants.IMG_CREDITS_CLICK);
        Image imgExit = new Image(Constants.IMG_EXIT);
        Image imgExitHover = new Image(Constants.IMG_EXIT_HOVER);
        Image imgExitClick = new Image(Constants.IMG_EXIT_CLICK);
        Image imgMusic = new Image(Constants.IMG_MUSIC);
        Image imgSFX = new Image(Constants.IMG_SFX);

        Button buttonStart = createImageButton(imgStart, imgStartHover, imgStartClick, 350, 220, 250, 60, 
            event -> showSelectCategoryScreen(primaryStage));
        Button buttonHowTo = createImageButton(imgHowTo, imgHowToHover, imgHowToClick, 350, 280, 250, 60, 
            event -> showHowToPlayScreen(primaryStage));
        Button buttonCredits = createImageButton(imgCredits, imgCreditsHover, imgCreditsClick, 350, 340, 250, 60, 
            event -> showCreditsScreen(primaryStage));
        Button buttonExit = createImageButton(imgExit, imgExitHover, imgExitClick, 350, 400, 250, 60, 
            event -> Platform.exit());
            
        Button buttonMusic = createImageButton(imgMusic, 870, 500, 30, 30);
        Button buttonSFX = createImageButton(imgSFX, 920, 500, 30, 30);

        pane.getChildren().addAll(buttonStart, buttonHowTo, buttonCredits, buttonExit, buttonMusic, buttonSFX);
    }

    // Method to add category buttons to the pane
    private static void addCategoryButtonsToPane(Pane pane, Stage primaryStage) {
        Image imgEasy = new Image(Constants.IMG_EASY);
        Image imgEasyHover = new Image(Constants.IMG_EASY_HOVER);
        Image imgEasyClick = new Image(Constants.IMG_EASY_CLICK);

        Image imgAverage = new Image(Constants.IMG_AVERAGE);
        Image imgAverageHover = new Image(Constants.IMG_AVERAGE_HOVER);
        Image imgAverageClick = new Image(Constants.IMG_AVERAGE_CLICK);

        Image imgDifficult = new Image(Constants.IMG_DIFFICULT);
        Image imgDifficultHover = new Image(Constants.IMG_DIFFICULT_HOVER);
        Image imgDifficultClick = new Image(Constants.IMG_DIFFICULT_CLICK);

        Image imgReturn = new Image(Constants.IMG_RETURN);
        Image imgReturnHover = new Image(Constants.IMG_RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.IMG_RETURN_CLICK);

        Button buttonEasy = createImageButton(imgEasy, imgEasyHover, imgEasyClick, (1000-250)/2, (600-240)/2, 250, 60, event -> showHangmanScreen(primaryStage));
        Button buttonAverage = createImageButton(imgAverage, imgAverageHover, imgAverageClick, (1000-250)/2,  (600-60)/2, 250, 60, event -> showHangmanScreen(primaryStage));
        Button buttonDifficult = createImageButton(imgDifficult, imgDifficultHover, imgDifficultClick, (1000-250)/2,  (600+120)/2, 250, 60, event -> showHangmanScreen(primaryStage));
        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30, event -> fadeToLobby(primaryStage));

        pane.getChildren().addAll(buttonEasy, buttonAverage, buttonDifficult, buttonReturn);
    }

    // Method to create an ImageView
    private static ImageView createImageView(String imagePath, double fitWidth, double fitHeight, boolean preserveRatio, double layoutX, double layoutY) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setLayoutX(layoutX);
        imageView.setLayoutY(layoutY);
        return imageView;
    }

    // Method to show the Hangman game screen
    public static void showHangmanScreen(Stage primaryStage) {
        // Dispose of the current Stage and create a new one for the Hangman game
        
        Hangman hangman = new Hangman(); 
        hangman.start(new Stage());
        
        primaryStage.close();  // Close the current JavaFX stage
    } 

    // public static void showHangmanScreen(Stage primaryStage) {
    //     // Create a fade-out transition for the current stage
    //     FadeTransition fadeOut = new FadeTransition(Duration.millis(500), primaryStage.getScene().getRoot());
    //     fadeOut.setFromValue(1.0);
    //     fadeOut.setToValue(0.0);
    //     fadeOut.setOnFinished(event -> {
    //         // Start the Hangman game in a new stage
    //         Hangman hangman = new Hangman();
    //         Stage hangmanStage = new Stage();
    //         hangman.start(hangmanStage);

    //         // Close the current stage
    //         primaryStage.close();

    //         // Create a fade-in transition for the new stage
    //         FadeTransition fadeIn = new FadeTransition(Duration.millis(500), hangmanStage.getScene().getRoot());
    //         fadeIn.setFromValue(0.0);
    //         fadeIn.setToValue(1.0);
    //         fadeIn.play();
    //     });
    //     fadeOut.play();
    // }
}