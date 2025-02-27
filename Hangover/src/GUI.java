
import javafx.animation.FadeTransition;
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
import javafx.stage.StageStyle;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.File;

// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;

public class GUI extends Application {

    static MusicPlayer player = new MusicPlayer();
    static MusicPlayer bgm = new MusicPlayer();

    private static boolean isMusicOn = true; // Initially true since music is playing by default
    private static boolean isSFXOn = true; // Initially true since SFX is playing by default

    private static boolean splashPlayed = false; // Flag to track splash screen state

    // private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // GUI.primaryStage = primaryStage; // Store the primary stage

        primaryStage.setTitle("Hangman Game");
        primaryStage.setWidth(Constants.FRAME_SIZE.width);
        primaryStage.setHeight(Constants.FRAME_SIZE.height);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
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

    private static void initFX(JFXPanel jfxPanel, Stage primaryStage) {
        if (splashPlayed) {
            // If splash has already played, go directly to the lobby
            fadeToLobby(primaryStage);
        } else {
            // Play splash only once
            splashPlayed = true;
            playSplashScreen(primaryStage);
        }
    }

    private static void playSplashScreen(Stage primaryStage) {
        MediaPlayer splashMediaPlayer = createMediaPlayer(Constants.SPLASH_VIDEO_PATH, true, 1);
        if (splashMediaPlayer == null)
            return;

        splashMediaPlayer.setStopTime(Duration.seconds(4)); // Set splash screen duration to 4 seconds

        MediaView splashMediaView = createMediaView(splashMediaPlayer, 1000, 600);
        StackPane splashPane = new StackPane(splashMediaView);
        Scene splashScene = new Scene(splashPane, Color.BLACK);
        primaryStage.setScene(splashScene);
        primaryStage.show();

        splashMediaPlayer.setOnEndOfMedia(() -> Platform.runLater(() -> fadeToLobby(primaryStage)));
    }

    public static void fadeToLobby(Stage primaryStage) {
        if (primaryStage == null) {
            System.err.println("Error: Primary stage is null.");
            return;
        }

        if (isMusicOn && !bgm.isPlaying()) { // Only play if music is ON
            bgm.playMusic(Constants.BGM);
        }

        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null)
            return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);
        Pane lobbyPane = new Pane(lobbyMediaView);

        ImageView imageView = createImageView("file:" + Constants.IMAGE_PATH, 700, 0, true, (1000 - 700) / 2, 10);
        lobbyPane.getChildren().add(imageView);

        addButtonsToPane(lobbyPane, primaryStage);

        Scene lobbyScene = new Scene(lobbyPane, Color.BLACK);
        applyFadeTransition1(primaryStage, lobbyScene);
    }

    private static void applyFadeTransition1(Stage stage, Scene newScene) {
        Scene currentScene = stage.getScene();

        if (currentScene != null) {
            // Fade out current scene smoothly
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.6), currentScene.getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.2); // Instead of going to 0, keep a bit of visibility for smoothness

            fadeOut.setOnFinished(event -> {
                // Set the new scene while the old one is still slightly visible
                stage.setScene(newScene);

                // Fade in new scene smoothly
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), newScene.getRoot());
                fadeIn.setFromValue(0.2); // Start from the slightly faded state
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play(); // Start fade-out transition
        } else {
            // If no current scene, just fade in the new one
            stage.setScene(newScene);
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), newScene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    private static void applyFadeTransition(Stage stage, Scene newScene) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: black;"); // Ensures a black fade

        Scene currentScene = stage.getScene();
        if (currentScene != null) {
            Pane currentRoot = (Pane) currentScene.getRoot();
            currentRoot.getChildren().add(overlay);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), overlay);
            fadeOut.setFromValue(0.0);
            fadeOut.setToValue(1.0);
            fadeOut.setOnFinished(event -> {
                stage.setScene(newScene);

                Pane newRoot = (Pane) newScene.getRoot();
                newRoot.getChildren().add(overlay); // Re-add overlay for fade-in

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), overlay);
                fadeIn.setFromValue(1.0);
                fadeIn.setToValue(0.0);
                fadeIn.setOnFinished(e -> newRoot.getChildren().remove(overlay)); // Remove overlay after fade-in
                fadeIn.play();
            });
            fadeOut.play();
        } else {
            // First-time scene load, fade in directly
            stage.setScene(newScene);
            Pane newRoot = (Pane) newScene.getRoot();
            newRoot.getChildren().add(overlay);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), overlay);
            fadeIn.setFromValue(1.0);
            fadeIn.setToValue(0.0);
            fadeIn.setOnFinished(e -> newRoot.getChildren().remove(overlay));
            fadeIn.play();
        }
    }

    private static void showSelectCategoryScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null)
            return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);

        // New image in the center of the screen
        Image centerImage = new Image("/resources1/SelectScreen.png");
        ImageView centerImageView = new ImageView(centerImage);
        centerImageView.setPreserveRatio(true);
        centerImageView.setFitWidth(900); // Adjust as needed
        centerImageView.setLayoutX((1000 - 900) / 2); // Center horizontally
        centerImageView.setLayoutY(9); // Center vertically

        Pane categoryPane = new Pane(lobbyMediaView, centerImageView);
        addCategoryButtonsToPane(categoryPane, primaryStage);

        Scene categoryScene = new Scene(categoryPane, 1000, 600);

        // Apply fade transition when switching to the category screen
        applyFadeTransition(primaryStage, categoryScene);
    }

    private static void showHowToPlayScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null)
            return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);

        ImageView howToPlayImageView = createImageView(Constants.IMG_HOW_TO_PLAY, 900, 540, true, 0, 0);

        Image imgReturn = new Image(Constants.IMG_RETURN);
        Image imgReturnHover = new Image(Constants.IMG_RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.IMG_RETURN_CLICK);

        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30,
                Constants.CLICK, event -> fadeToLobby(primaryStage));

        // StackPane centers items automatically
        StackPane howToPane = new StackPane(lobbyMediaView, howToPlayImageView, buttonReturn);
        howToPane.setPrefSize(1000, 600);
        StackPane.setAlignment(buttonReturn, Pos.TOP_LEFT); // Move button to top-left

        Scene howToScene = new Scene(howToPane, 1000, 600);
        primaryStage.setScene(howToScene);
    }

    private static void showCreditsScreen(Stage primaryStage) {
        MediaPlayer lobbyMediaPlayer = createMediaPlayer(Constants.LOBBY_VIDEO_PATH, true, MediaPlayer.INDEFINITE);
        if (lobbyMediaPlayer == null)
            return;

        MediaView lobbyMediaView = createMediaView(lobbyMediaPlayer, 1000, 600);

        ImageView creditsImageView = createImageView(Constants.IMG_CREDITS_SCREEN, 900, 540, true, 0, 0);

        Image imgReturn = new Image(Constants.IMG_RETURN);
        Image imgReturnHover = new Image(Constants.IMG_RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.IMG_RETURN_CLICK);

        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30,
                Constants.CLICK, event -> fadeToLobby(primaryStage));

        // StackPane centers items automatically
        StackPane creditsPane = new StackPane(lobbyMediaView, creditsImageView, buttonReturn);
        creditsPane.setPrefSize(1000, 600);
        StackPane.setAlignment(buttonReturn, Pos.TOP_LEFT); // Move button to top-left

        Scene creditsScene = new Scene(creditsPane, 1000, 600);
        primaryStage.setScene(creditsScene);
    }

    // Overloaded method to create an image button with hover and click effects
    private static Button createImageButton(Image image, Image hoverImage, Image clickImage, double x, double y,
            double width, double height, String soundPath, EventHandler<ActionEvent> action) {
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
            button.setOnAction(event -> {
                if (soundPath != null && !soundPath.isEmpty() && isSFXOn) {
                    player.playSoundEffect(soundPath); // Play sound effect
                }
                action.handle(event);
            });
        }

        return button;
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
        Image imgMusicOff = new Image(Constants.IMG_MUSIC_OFF);
        Image imgMusicClick = new Image(Constants.IMG_MUSIC_CLICK);
        Image imgSFX = new Image(Constants.IMG_SFX);
        Image imgSFXOff = new Image(Constants.IMG_SFX_OFF);
        Image imgSFXClick = new Image(Constants.IMG_SFX_CLICK);

        Button buttonStart = createImageButton(imgStart, imgStartHover, imgStartClick, 350, 220, 250, 60,
                Constants.CLICK,
                event -> showSelectCategoryScreen(primaryStage));
        Button buttonHowTo = createImageButton(imgHowTo, imgHowToHover, imgHowToClick, 350, 280, 250, 60,
                Constants.CLICK,
                event -> showHowToPlayScreen(primaryStage));
        Button buttonCredits = createImageButton(imgCredits, imgCreditsHover, imgCreditsClick, 350, 340, 250, 60,
                Constants.CLICK,
                event -> showCreditsScreen(primaryStage));
        Button buttonExit = createImageButton(imgExit, imgExitHover, imgExitClick, 350, 400, 250, 60, Constants.CLICK,
                event -> Platform.exit());

        Button buttonMusic = createMusicButton(imgMusic, imgMusicClick, imgMusicOff, 870, 500, 30, 30, Constants.CLICK,
                event -> {
                    if (isMusicOn) {
                        bgm.resumeMusic();
                    } else {
                        bgm.pauseMusic();
                    }
                });

        buttonMusic.setOnAction(event -> {
            if (isMusicOn) {
                bgm.pauseMusic();
                setButtonGraphic(buttonMusic, imgMusicOff, 30, 30); // Set to MusicOff when paused
            } else {
                bgm.resumeMusic();
                setButtonGraphic(buttonMusic, imgMusic, 30, 30); // Set to MusicOn when playing
            }
            isMusicOn = !isMusicOn;
        });

        Button buttonSFX = createSFXButton(imgSFX, imgSFXClick, imgSFXOff, 920, 500, 30, 30, Constants.CLICK, null);

        buttonSFX.setOnAction(event -> {
            if (isSFXOn) {
                setButtonGraphic(buttonSFX, imgSFXOff, 30, 30); // Set to MusicOff when paused
            } else {
                setButtonGraphic(buttonSFX, imgSFX, 30, 30); // Set to MusicOn when playing
            }
            isSFXOn = !isSFXOn;
        });

        pane.getChildren().addAll(buttonStart, buttonHowTo, buttonCredits, buttonExit, buttonMusic, buttonSFX);
    }

    private static Button createMusicButton(Image image, Image hoverImage, Image clickImage, double x, double y,
            double width, double height, String soundPath, EventHandler<ActionEvent> action) {
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

        button.setOnMouseEntered(event -> setButtonGraphic(button, hoverImage, width, height));

        button.setOnMouseExited(event -> {
            // Return to the correct state (MusicOn or MusicOff) when not hovered
            setButtonGraphic(button, isMusicOn ? image : clickImage, width, height);
        });

        button.setOnMouseExited(event -> {
            // Return to the correct state (MusicOn or MusicOff) when not hovered
            setButtonGraphic(button, isMusicOn ? image : clickImage, width, height);
        });

        if (action != null) {
            button.setOnAction(event -> {
                // Play sound effect safely
                if (soundPath != null && !soundPath.isEmpty()) {
                    Platform.runLater(() -> player.playSoundEffect(soundPath));
                }

                // Toggle music on/off
                isMusicOn = !isMusicOn;
                if (isMusicOn) {
                    bgm.resumeMusic();
                    setButtonGraphic(button, image, width, height); // Set to MusicOn
                } else {
                    bgm.pauseMusic();
                    setButtonGraphic(button, clickImage, width, height); // Set to MusicOff
                }

                // Handle additional action if provided
                action.handle(event);
            });
        }
        return button;
    }

    private static Button createSFXButton(Image image, Image hoverImage, Image clickImage, double x, double y,
            double width, double height, String soundPath, EventHandler<ActionEvent> action) {
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

        button.setOnMouseEntered(event -> setButtonGraphic(button, hoverImage, width, height));

        button.setOnMouseExited(event -> {
            // Return to the correct state (MusicOn or MusicOff) when not hovered
            setButtonGraphic(button, isSFXOn ? image : clickImage, width, height);
        });

        button.setOnMouseExited(event -> {
            // Return to the correct state (MusicOn or MusicOff) when not hovered
            setButtonGraphic(button, isSFXOn ? image : clickImage, width, height);
        });

        if (action != null) {
            button.setOnAction(event -> {
                if (soundPath != null && !soundPath.isEmpty()) {
                    player.playSoundEffect(soundPath);
                }
                action.handle(event);

                // Toggle music state
                isSFXOn = !isSFXOn;

                // Set button graphic based on new state
                if (isSFXOn) {
                    setButtonGraphic(button, image, width, height); // MusicOn
                } else {
                    setButtonGraphic(button, clickImage, width, height); // MusicOff
                }
            });
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

        Button buttonEasy = createImageButton(imgEasy, imgEasyHover, imgEasyClick, (1000 - 250) / 2,
                ((600 - 240) / 2) + 20,
                250, 60, Constants.CLICK, event -> showHangmanScreen(primaryStage, "easy"));
        Button buttonAverage = createImageButton(imgAverage, imgAverageHover, imgAverageClick, (1000 - 250) / 2,
                ((600 - 60) / 2) + 20, 250, 60, Constants.CLICK, event -> showHangmanScreen(primaryStage, "average"));
        Button buttonDifficult = createImageButton(imgDifficult, imgDifficultHover, imgDifficultClick, (1000 - 250) / 2,
                ((600 + 120) / 2) + 20, 250, 60, Constants.CLICK,
                event -> showHangmanScreen(primaryStage, "difficult"));

        Button buttonReturn = createImageButton(imgReturn, imgReturnHover, imgReturnClick, 5, 10, 30, 30,
                Constants.CLICK, event -> fadeToLobby(primaryStage));

        pane.getChildren().addAll(buttonEasy, buttonAverage, buttonDifficult, buttonReturn);
    }

    // Method to create an ImageView
    private static ImageView createImageView(String imagePath, double fitWidth, double fitHeight, boolean preserveRatio,
            double layoutX, double layoutY) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setLayoutX(layoutX);
        imageView.setLayoutY(layoutY);
        return imageView;
    }

    // Method to show the Hangman game screen
    public static void showHangmanScreen(Stage primaryStage, String category) {

        bgm.stopMusic();
        primaryStage.close();
        Hangman hangman = new Hangman(category); // Pass category in constructor
        hangman.start(new Stage());
    }
}