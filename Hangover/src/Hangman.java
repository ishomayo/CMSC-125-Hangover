
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
// import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
// import javafx.scene.text.Font;
import javafx.stage.Stage;
// import javafx.stage.StageStyle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.HashMap;
import java.util.function.UnaryOperator;

// import javax.swing.JLabel;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class Hangman extends Application {
    private int incorrectGuesses;
    private int score = 0;
    private String[] wordChallenge;
    private final WordDB wordDB = new WordDB();
    private Label hangmanImage, categoryLabel, hiddenWordLabel, scoreLabel, timerLabel;
    private HashMap<Character, Label> letterLabels;
    private javafx.scene.control.TextField inputField;
    private Button enterButton;
    private TextField secondsLeft, scoreField;
    private int secondsT = 0;
    private Stage primaryStage;

    javafx.animation.Timeline timer1 = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(
                    javafx.util.Duration.seconds(1),
                    event -> {
                        secondsT++;
                        secondsLeft.setText(String.valueOf(secondsT));
                    }));

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hangman Game");
        primaryStage.setWidth(CommonConstants.FRAME_SIZE.width);
        primaryStage.setHeight(CommonConstants.FRAME_SIZE.height);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(event -> {
            // Load the Lobby Screen when the window is closed
            try {
                new GUI().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Step 1: Set up the root pane and scene
        Pane root = new Pane();
        Scene scene = new Scene(root);

        Image backgroundImage = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\InGame_Screen.jpg");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        root.setBackground(new Background(background));

        // Step 2: Create a black rectangle that covers the entire scene
        Rectangle blackFade = new Rectangle(CommonConstants.FRAME_SIZE.width, CommonConstants.FRAME_SIZE.height);
        blackFade.setFill(Color.BLACK);
        root.getChildren().add(blackFade); // Add the black rectangle to the scene

        // Step 3: Set up and show the scene
        primaryStage.setScene(scene);
        primaryStage.show();

        // Step 4: Apply fade-out transition on the black rectangle
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), blackFade);
        fadeOut.setFromValue(1.0); // Start fully visible (black)
        fadeOut.setToValue(0.0); // Fade to fully transparent
        fadeOut.setOnFinished(event -> root.getChildren().remove(blackFade)); // Remove the black rectangle after
                                                                              // fade-out
        fadeOut.play(); // Start the fade-out transition

        // Step 5: Set up the rest of your game logic
        wordChallenge = wordDB.loadChallenge();
        letterLabels = new HashMap<>();
        addGuiComponents(root);
    }

    private void addGuiComponents(Pane root) {
        // Hangman image
        hangmanImage = new Label();
        ImageView hangmanImageView = new ImageView(new Image(CommonConstants.IMAGE_PATH));
        hangmanImageView.setFitWidth(300); // Set the desired width
        hangmanImageView.setFitHeight(600); // Set the desired height
        hangmanImageView.setPreserveRatio(true); // Preserve the aspect ratio
        hangmanImage.setGraphic(hangmanImageView);
        hangmanImage.setLayoutX(200); // Set the desired X position
        hangmanImage.setLayoutY(10); // Set the desired Y position
        root.getChildren().add(hangmanImage);

        scaleHangmanImage(incorrectGuesses);

        // Score label
        scoreLabel = new Label("Score: ");
        scoreLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setLayoutX(765);
        scoreLabel.setLayoutY(53);
        scoreLabel.setPrefWidth(200);
        scoreLabel.setPrefHeight(30);
        root.getChildren().add(scoreLabel);

        scoreField = new TextField();
        scoreField.setText(String.valueOf(score));
        scoreField.setEditable(false);
        scoreField.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: black;");
        scoreField.setLayoutX(840);
        scoreField.setLayoutY(46);
        scoreField.setPrefWidth(60);
        scoreField.setPrefHeight(30);
        root.getChildren().add(scoreField);

        timer1.setCycleCount(javafx.animation.Animation.INDEFINITE);

        // Timer label
        timer1.play();
        timerLabel = new Label("Time: ");
        timerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        timerLabel.setTextFill(Color.BLACK);
        timerLabel.setLayoutX(400);
        timerLabel.setLayoutY(50);
        timerLabel.setPrefWidth(200);
        timerLabel.setPrefHeight(30);
        root.getChildren().add(timerLabel);

        secondsLeft = new TextField();
        secondsLeft.setEditable(false);
        secondsLeft.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: transparent; -fx-text-fill: black;");
        secondsLeft.setLayoutX(470);
        secondsLeft.setLayoutY(40);
        secondsLeft.setPrefWidth(60);
        secondsLeft.setPrefHeight(30);
        root.getChildren().add(secondsLeft);

        // Category label
        categoryLabel = new Label("Category: " + wordChallenge[0]);
        categoryLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        categoryLabel.setTextFill(Color.BLACK);
        categoryLabel.setLayoutX(60);
        categoryLabel.setLayoutY(50);
        categoryLabel.setPrefWidth(400);
        categoryLabel.setPrefHeight(30);
        root.getChildren().add(categoryLabel);

        // Hidden word label
        hiddenWordLabel = new Label(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");
        hiddenWordLabel.setTextFill(Color.BLACK);
        hiddenWordLabel.setLayoutX(150);
        hiddenWordLabel.setLayoutY(100);
        hiddenWordLabel.setPrefWidth(500);
        hiddenWordLabel.setPrefHeight(50);
        root.getChildren().add(hiddenWordLabel);

        // Letter panel for displaying letter icons A - I
        GridPane letterPanelA_I = new GridPane();
        letterPanelA_I.setLayoutX(50);
        letterPanelA_I.setLayoutY(200);
        letterPanelA_I.setHgap(5);
        letterPanelA_I.setVgap(5);
        letterPanelA_I.setPrefWidth(400);
        letterPanelA_I.setPrefHeight(400);
        letterPanelA_I.setStyle("-fx-background-color: transparent;");

        // Loop through each letter from A to I
        for (char c = 'A'; c <= 'J'; c++) {
            Label letterLabel = createLetterLabel(c);
            letterPanelA_I.add(letterLabel, c - 'A', 0);
        }
        root.getChildren().add(letterPanelA_I);

        // Letter panel for displaying letter icons J - S
        GridPane letterPanelJ_S = new GridPane();
        letterPanelJ_S.setLayoutX(50);
        letterPanelJ_S.setLayoutY(250);
        letterPanelJ_S.setHgap(5);
        letterPanelJ_S.setVgap(5);
        letterPanelJ_S.setPrefWidth(400);
        letterPanelJ_S.setPrefHeight(400);
        letterPanelJ_S.setStyle("-fx-background-color: transparent;");

        // Loop through each letter from J to S
        for (char c = 'K'; c <= 'T'; c++) {
            Label letterLabel = createLetterLabel(c);
            letterPanelJ_S.add(letterLabel, c - 'K', 0);
        }
        root.getChildren().add(letterPanelJ_S);

        // Letter panel for displaying letter icons T - Z
        GridPane letterPanelT_Z = new GridPane();
        letterPanelT_Z.setLayoutX(150);
        letterPanelT_Z.setLayoutY(300);
        letterPanelT_Z.setHgap(10);
        letterPanelT_Z.setVgap(10);
        letterPanelT_Z.setPrefWidth(400);
        letterPanelT_Z.setPrefHeight(400);
        letterPanelT_Z.setStyle("-fx-background-color: transparent;");

        // Loop through each letter from T to Z
        for (char c = 'U'; c <= 'Z'; c++) {
            Label letterLabel = createLetterLabel(c);
            letterPanelT_Z.add(letterLabel, c - 'U', 0);
        }
        root.getChildren().add(letterPanelT_Z);

        // Input field for letter guesses
        inputField = new javafx.scene.control.TextField();
        inputField.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-alignment: center; " +
                        "-fx-border-color: black; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5px; " +
                        "-fx-padding: 5px;" +
                        "-fx-focus-color: transparent; " + // Remove blue focus color
                        "-fx-faint-focus-color: transparent;");
        inputField.setLayoutX(250);
        inputField.setLayoutY(400);
        inputField.setPrefWidth(50);
        inputField.setPrefHeight(50);

        inputField.setLayoutX(250);
        inputField.setLayoutY(400);

        // Once the window is displayed, ensure focus on the input field
        Platform.runLater(() -> inputField.requestFocus());

        // Restrict input to a single letter, auto-uppercase, and replace old value
        inputField.setOnKeyTyped(event -> {
            String input = event.getCharacter().toUpperCase();

            if (!input.matches("[a-zA-Z]")) {
                event.consume(); // Ignore invalid input
                return;
            }

            inputField.setText(input); // Always replace with the new character

            inputField.positionCaret(1);

            event.consume();
        });

        UnaryOperator<TextFormatter.Change> letterFilter = change -> {
            String newText = change.getControlNewText().toUpperCase();

            // Allow only a single uppercase letter (A-Z)
            if (newText.matches("[A-Z]?")) {
                return change;
            }
            return null; // Reject invalid input
        };

        inputField.setTextFormatter(new TextFormatter<>(letterFilter));

        // Make sure the text field is focused when the game starts
        inputField.requestFocus();

        // Bind Enter key to the enter button
        inputField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    enterButton.fire(); // Simulate button click
                    break;
                default:
                    break;
            }
        });

        root.getChildren().add(inputField);

        Image defaultIcon = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\default_icon.png");
        Image hoverIcon = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\hover_icon.png");
        Image clickedIcon = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\clicked_icon.png");

        // Enter button with different state icons

        enterButton = new Button();
        ImageView imageViewDefault = new ImageView(defaultIcon);
        ImageView imageViewHover = new ImageView(hoverIcon);
        ImageView imageViewClicked = new ImageView(clickedIcon);

        enterButton.setPrefHeight(60); // Set height of the button

        // Set the image to adjust its aspect ratio while maintaining the height
        imageViewDefault.setFitHeight(60); // Set the height of the ImageView to 50
        imageViewDefault.setPreserveRatio(true); // Keep the aspect ratio of the image

        imageViewHover.setFitHeight(60); // Set the height of the ImageView to 50
        imageViewHover.setPreserveRatio(true);

        imageViewClicked.setFitHeight(60); // Set the height of the ImageView to 50
        imageViewClicked.setPreserveRatio(true);

        enterButton.setGraphic(imageViewDefault);
        enterButton.setLayoutX(300);
        enterButton.setLayoutY(395);
        enterButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        enterButton.setOnMouseEntered(event -> enterButton.setGraphic(imageViewHover));
        enterButton.setOnMouseExited(event -> enterButton.setGraphic(imageViewDefault));
        enterButton.setOnMousePressed(event -> enterButton.setGraphic(imageViewClicked));
        enterButton.setOnMouseReleased(event -> enterButton.setGraphic(imageViewDefault));
        enterButton.setOnAction(event -> handleEnterButtonAction(enterButton));
        root.getChildren().add(enterButton);
    }

    private Label createLetterLabel(char c) {
        Image originalImage = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + c + "_default.png");

        // Scale the image to fit the Label size
        ImageView imageView = new ImageView(originalImage);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setPreserveRatio(true);

        Label letterLabel = new Label();
        letterLabel.setGraphic(imageView);
        letterLabels.put(c, letterLabel);
        return letterLabel;
    }

    private void scaleHangmanImage(int incorrectGuesses) {
        // Get the size of the container (window size)
        int panelWidth = (int) (hangmanImage.getParent().getLayoutBounds().getWidth() - 150); // Add padding if
                                                                                              // necessary
        int panelHeight = (int) (hangmanImage.getParent().getLayoutBounds().getHeight() - 200); // Adjust to leave space
                                                                                                // for other components

        // Define max width and height constraints
        int maxWidth = 500;
        int maxHeight = 1000;

        // Make sure the width and height don't exceed the constraints
        if (panelWidth > maxWidth)
            panelWidth = maxWidth;
        if (panelHeight > maxHeight)
            panelHeight = maxHeight;

        // Get the image corresponding to the current incorrect guess
        String imagePath = "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources\\"
                + (incorrectGuesses + 1) + ".png";
        Image originalImage = new Image(imagePath);

        // Calculate scaling factor to maintain aspect ratio
        double aspectRatio = originalImage.getWidth() / originalImage.getHeight();

        if (panelWidth / aspectRatio <= panelHeight) {
            // Scale based on width
            panelHeight = (int) (panelWidth / aspectRatio);
        } else {
            // Scale based on height
            panelWidth = (int) (panelHeight * aspectRatio);
        }

        // Scale the image to fit the panel while maintaining aspect ratio
        Image scaledImage = new Image(imagePath, panelWidth, panelHeight, true, true);

        // Set the scaled image to the Label
        hangmanImage.setGraphic(new ImageView(scaledImage));
        hangmanImage.setLayoutX(690);
        hangmanImage.setLayoutY(150);
    }

    private void handleEnterButtonAction(Button enterButton) {
        String input = inputField.getText().toUpperCase();
        if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'Z') {

            MusicPlayer musicPlayer = new MusicPlayer();
            MusicPlayer impendingDoomPlayer = new MusicPlayer();

            char guessedLetter = input.charAt(0);
            inputField.setText(""); // Clear the input field

            // Load the image based on the correct or incorrect state
            String imagePath = "";

            if (wordChallenge[1].contains(String.valueOf(guessedLetter))) {
                // Correct guess
                imagePath = "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + guessedLetter
                        + "_correct.png";
            } else {
                // Incorrect guess
                imagePath = "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + guessedLetter
                        + "_incorrect.png";
                incorrectGuesses++;
                if (score > 4) {
                    score = score - 5;
                } else {
                    score = 0;
                }

                // Call the updated scale method for hangman image
                scaleHangmanImage(incorrectGuesses); // Scale based on number of incorrect guesses

                if (incorrectGuesses >= 5) {
                    // Start playing the impending doom sound in a loop
                    impendingDoomPlayer.playMusic(Constants.IMPEND);
                }

                if (incorrectGuesses >= 6) {
                    if (impendingDoomPlayer != null) {
                        impendingDoomPlayer.stopMusic();
                    }

                    musicPlayer.playSoundEffect(Constants.GAMEOVER);

                    // Switch to the result screen
                    showResultScreen(false); // false = game over
                    return;
                }

            }

            // Load and scale the icon
            // Image originalImage = new Image(imagePath);
            Image scaledImage = new Image(imagePath, 50, 50, true, true);

            // Set the scaled icon to the corresponding letter
            letterLabels.get(guessedLetter).setGraphic(new ImageView(scaledImage));
            updateHiddenWord(guessedLetter);

            if (!hiddenWordLabel.getText().contains("_")) {
                timer1.stop();
                if (secondsT == 1) {
                    score += 1;
                } else {
                    score = score + (60 - secondsT);
                }
                scoreField.setText(String.valueOf(score));
                resetGame(enterButton);
            }
        }
    }

    private void showResultScreen(boolean isWin) {
        StackPane resultRoot = new StackPane();
        Scene resultScene = new Scene(resultRoot, CommonConstants.FRAME_SIZE.width, CommonConstants.FRAME_SIZE.height);

        // Background Image
        Image backgroundImage = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Result_Screen.png");
        System.out.println("Image loaded: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight()); // Debugging

        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        resultRoot.setBackground(new Background(background));

        // Score Display
        Label scoreTextLabel = new Label("Score: ");
        scoreTextLabel.setStyle("-fx-font-size: 24px;");
        scoreTextLabel.setTextFill(Color.BLACK);
        scoreTextLabel.setAlignment(Pos.CENTER);
        StackPane.setMargin(scoreTextLabel, new Insets(0, 0, 150, 50));

        Label scoreValueLabel = new Label(String.valueOf(score + 30));
        scoreValueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        scoreValueLabel.setTextFill(Color.BLACK);
        scoreValueLabel.setAlignment(Pos.CENTER);
        StackPane.setMargin(scoreValueLabel, new Insets(0, 0, 150, 150));

        // Images
        Image imgPlayAgain = new Image(Constants.PLAYAGAIN);
        Image imgPlayAgainHover = new Image(Constants.PLAYAGAIN_HOVER);
        Image imgPlayAgainClick = new Image(Constants.PLAYAGAIN_CLICK);

        Button playAgainButton = createImageButton(imgPlayAgain, imgPlayAgainHover, imgPlayAgainClick,
                250, 100, 250, 60, Constants.CLICK,
                event -> {
                    primaryStage.close();
                    Hangman hangman = new Hangman();
                    Stage newStage = new Stage();
                    hangman.start(newStage);
                });

        Image imgReturn = new Image(Constants.RETURN);
        Image imgReturnHover = new Image(Constants.RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.RETURN_CLICK);

        Button returnButton = createImageButton(imgReturn, imgReturnHover, imgReturnClick,
                250, 100, 250, 60, Constants.CLICK,
                event -> {
                    GUI gui = new GUI();
                    gui.start(new Stage());
                    primaryStage.close();
                });

        // Layout
        VBox layout = new VBox(15, playAgainButton, returnButton);
        layout.setAlignment(Pos.CENTER);

        // Shift downward
        StackPane root = new StackPane(layout);
        StackPane.setMargin(layout, new Insets(145, 0, 0, 0)); // Adjust downward// Move it slightly downward

        resultRoot.getChildren().add(layout);

        resultRoot.getChildren().add(scoreTextLabel);
        resultRoot.getChildren().add(scoreValueLabel);

        // Show Scene
        primaryStage.setScene(resultScene);
    }

    static MusicPlayer player = new MusicPlayer();

    private static void setButtonGraphic(Button button, Image image, double width, double height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        button.setGraphic(imageView);
    }

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
                if (soundPath != null && !soundPath.isEmpty()) {
                    player.playSoundEffect(soundPath); // Play sound effect
                }
                action.handle(event);
            });
        }

        return button;
    }

    private void updateHiddenWord(char guessedLetter) {
        char[] hiddenWord = hiddenWordLabel.getText().toCharArray();
        for (int i = 0; i < wordChallenge[1].length(); i++) {
            if (wordChallenge[1].charAt(i) == guessedLetter) {
                hiddenWord[i] = guessedLetter;
            }
        }
        hiddenWordLabel.setText(String.valueOf(hiddenWord));
    }

    private void resetGame(Button enterButton) {
        // Reload the word challenge
        wordChallenge = wordDB.loadChallenge();
        incorrectGuesses = 0;

        secondsT = 0;
        timer1.playFromStart();

        scaleHangmanImage(incorrectGuesses); // Apply scaling to the image

        // Reset the hidden word
        hiddenWordLabel.setText(CustomTools.hideWords(wordChallenge[1]));

        // Reset letter labels (back to default state)
        letterLabels.forEach((k, v) -> {
            // Reset the label with the default image and state
            Image defaultImage = new Image(
                    "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + k + "_default.png");
            ImageView imageView = new ImageView(defaultImage);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            v.setGraphic(imageView);

            // Ensure label is interactive again
            v.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        });

        enterButton.setOnAction(event -> handleEnterButtonAction(enterButton));

        // Optionally, clear the input field
        inputField.setText("");
    }

    public static void showHangmanScreen(Stage primaryStage) {
        // Dispose of the current Stage and create a new one for the Hangman game

        Hangman hangman = new Hangman();
        hangman.start(new Stage());

        primaryStage.close(); // Close the current JavaFX stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}
