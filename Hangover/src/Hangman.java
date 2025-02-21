
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
// import javafx.scene.media.Media;
// import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
// import javafx.scene.text.TextAlignment;
// import javafx.scene.text.Font;
import javafx.stage.Stage;
// import javafx.stage.StageStyle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

// import java.io.File;
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
    private HashMap<Character, Label> letterLabels;
    private javafx.scene.control.TextField inputField;
    private Button enterButton;
    private int secondsT = 0;
    private Stage primaryStage;
    private static String category;
    private Font labelFont = Font.loadFont("file:" + Constants.FONT, 18);
    private Font valueFont = Font.loadFont("file:" + Constants.FONT, 28);
    private HighScore highScore;
    private Label hangmanImage, categoryLabel, hiddenWordLabel, scoreTextLabel, timerLabel, highScoreLabel;
    private Pane hiddenWordPane, letterPane1, letterPane2, letterPane3;

    private static boolean isMusicOn = true; // Initially true since music is playing by default

    private TextField secondsLeft, scoreValue;

    static MusicPlayer bgm = new MusicPlayer();

    public void setCategory(String category) {
        Hangman.category = category;
    }

    public Hangman(String category) {
        Hangman.category = category;
    }

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
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\InGame_Screen.jpg");
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
        wordChallenge = wordDB.loadChallenge(category);
        letterLabels = new HashMap<>();
        addGuiComponents(root);
    }

    private void addGuiComponents(Pane root) {
        if (isMusicOn && !bgm.isPlaying()) { // Only play if music is ON
            bgm.playMusic(Constants.INGAMEBGM);
        }

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

        // Label for "Score:"
        scoreTextLabel = new Label("Score:");
        scoreTextLabel.setFont(labelFont);
        scoreTextLabel.setStyle("-fx-text-fill: BLACK;");
        scoreTextLabel.setLayoutX(735);
        scoreTextLabel.setLayoutY(53);
        scoreTextLabel.setPrefWidth(100);
        scoreTextLabel.setPrefHeight(30);
        root.getChildren().add(scoreTextLabel);

        String scoreStyle = "-fx-font-size: 24px; -fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: BLACK;";

        // Label for the score value
        scoreValue = new TextField();
        scoreValue.setStyle("-fx-font-family: '" + valueFont.getFamily() + "'; " + scoreStyle);
        scoreValue.setText(String.valueOf(score));
        scoreValue.setEditable(false);
        scoreValue.setAlignment(Pos.CENTER);
        scoreValue.setLayoutX(820);
        scoreValue.setLayoutY(45);
        scoreValue.setPrefWidth(110);
        scoreValue.setFocusTraversable(false);
        root.getChildren().add(scoreValue);

        timer1.setCycleCount(javafx.animation.Animation.INDEFINITE);

        timer1.play();

        // Label for "Time:"
        timerLabel = new Label("Time:");
        timerLabel.setFont(labelFont);
        timerLabel.setStyle("-fx-text-fill: RED;");
        timerLabel.setLayoutX(455);
        timerLabel.setLayoutY(55);
        timerLabel.setPrefWidth(100);
        timerLabel.setPrefHeight(30);
        root.getChildren().add(timerLabel);

        String timeStyle = "-fx-font-size: 20px; -fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: RED;";

        // Label for the seconds left
        secondsLeft = new TextField();
        secondsLeft.setStyle("-fx-font-family: '" + valueFont.getFamily() + "'; " + timeStyle);
        secondsLeft.setText("555");
        secondsLeft.setEditable(false);
        secondsLeft.setAlignment(Pos.CENTER);
        secondsLeft.setLayoutX(515);
        secondsLeft.setLayoutY(50);
        secondsLeft.setPrefWidth(80);
        secondsLeft.setFocusTraversable(false);
        root.getChildren().add(secondsLeft);

        // Category label
        categoryLabel = new Label(wordChallenge[0]);
        categoryLabel.setFont(labelFont);
        categoryLabel.setTextFill(Color.BLACK);
        categoryLabel.setLayoutX(65);
        categoryLabel.setLayoutY(55);
        categoryLabel.setPrefWidth(400);
        categoryLabel.setPrefHeight(30);
        root.getChildren().add(categoryLabel);

        // High Score label
        highScore = new HighScore();
        highScoreLabel = new Label("High Score: " + String.valueOf(highScore.showHighScore(category)));
        highScoreLabel.setFont(labelFont);
        highScoreLabel.setTextFill(Color.BLACK);
        highScoreLabel.setLayoutX(65);
        highScoreLabel.setLayoutY(90);
        highScoreLabel.setPrefWidth(400);
        highScoreLabel.setPrefHeight(30);
        root.getChildren().add(highScoreLabel);

        // Hidden word pane (StackPane centers its children automatically)
        hiddenWordPane = new StackPane();
        hiddenWordPane.setPrefSize(500, 50);
        hiddenWordPane.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; " +
                "-fx-background-color: rgba(255, 255, 255, 0.5); " + // 50% opacity black background
                "-fx-text-fill: white; " + // Make text white
                "-fx-padding: 10px; " + // Padding for better visibility
                "-fx-border-color: black; " + // White outline
                "-fx-border-width: 2px;"); // Outline thickness
        hiddenWordPane.setLayoutX(75);
        hiddenWordPane.setLayoutY(125);
        root.getChildren().add(hiddenWordPane);

        // Hidden word label
        hiddenWordLabel = new Label(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setStyle("-fx-font-size: 35px; -fx-font-weight: bold;");
        hiddenWordLabel.setTextFill(Color.BLACK);

        // Automatically center label inside the pane
        hiddenWordPane.getChildren().add(hiddenWordLabel);

        // Create the StackPane for letterPanelA_I
        letterPane1 = new StackPane();
        letterPane1.setPrefSize(400, 50);
        letterPane1.setStyle("-fx-background-color: transparent;");
        letterPane1.setLayoutX(80); // Position of the StackPane
        letterPane1.setLayoutY(240); // Position of the StackPane
        root.getChildren().add(letterPane1);

        // Letter panel for displaying letter icons A - I
        GridPane letterPanelA_I = new GridPane();
        letterPanelA_I.setHgap(5); // Horizontal gap between columns
        letterPanelA_I.setVgap(5); // Optional vertical gap between rows (if needed)
        letterPanelA_I.setStyle("-fx-background-color: transparent;");

        // Set the preferred width and allow dynamic resizing of columns
        int cols = 9; // Number of columns (for A - I)
        for (int i = 0; i < cols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / cols); // Divide width equally
            columnConstraints.setHgrow(Priority.ALWAYS); // Allow columns to grow
            letterPanelA_I.getColumnConstraints().add(columnConstraints);
        }

        // Add labels for letters A - I
        for (char c = 'A'; c <= 'I'; c++) { // Loop from 'A' to 'I'
            Label letterLabel = createLetterLabel(c); // Assuming you have a method that creates the label
            letterPanelA_I.add(letterLabel, c - 'A', 0); // Position labels in each column
        }

        // Add the GridPane to the StackPane
        letterPane1.getChildren().add(letterPanelA_I);

        // Create the StackPane for letterPanelJ_R
        letterPane2 = new StackPane();
        letterPane2.setPrefSize(400, 50);
        letterPane2.setStyle("-fx-background-color: transparent;");
        letterPane2.setLayoutX(75); // Position of the StackPane
        letterPane2.setLayoutY(300); // Position of the StackPane
        root.getChildren().add(letterPane2);

        // Letter panel for displaying letter icons J - R
        GridPane letterPanelJ_R = new GridPane();
        letterPanelJ_R.setHgap(5); // Horizontal gap between columns
        letterPanelJ_R.setVgap(5); // Optional vertical gap between rows (if needed)
        letterPanelJ_R.setStyle("-fx-background-color: transparent;");

        // Set the preferred width and allow dynamic resizing of columns
        cols = 9; // Number of columns (for J - R)
        for (int i = 0; i < cols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / cols); // Divide width equally
            columnConstraints.setHgrow(Priority.ALWAYS); // Allow columns to grow
            letterPanelJ_R.getColumnConstraints().add(columnConstraints);
        }

        // Add labels for letters J - R
        for (char c = 'J'; c <= 'R'; c++) { // Loop from 'J' to 'R'
            Label letterLabel = createLetterLabel(c); // Assuming you have a method that creates the label
            letterPanelJ_R.add(letterLabel, c - 'J', 0); // Position labels in each column
        }

        // Add the GridPane to the StackPane
        letterPane2.getChildren().add(letterPanelJ_R);

        // Create the StackPane for letterPanelS_Z
        letterPane3 = new StackPane();
        letterPane3.setPrefSize(350, 50);
        letterPane3.setStyle("-fx-background-color: transparent;");
        letterPane3.setLayoutX(85); // Position of the StackPane
        letterPane3.setLayoutY(360); // Position of the StackPane
        root.getChildren().add(letterPane3);

        // Letter panel for displaying letter icons S - Z
        GridPane letterPanelS_Z = new GridPane();
        letterPanelS_Z.setHgap(10); // Horizontal gap between columns
        letterPanelS_Z.setVgap(10); // Optional vertical gap between rows (if needed)
        letterPanelS_Z.setStyle("-fx-background-color: transparent;");

        // Set the preferred width and allow dynamic resizing of columns
        cols = 8; // Number of columns (for S - Z)
        for (int i = 0; i < cols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / cols); // Divide width equally
            columnConstraints.setHgrow(Priority.ALWAYS); // Allow columns to grow
            letterPanelS_Z.getColumnConstraints().add(columnConstraints);
        }

        // Add labels for letters S - Z
        for (char c = 'S'; c <= 'Z'; c++) { // Loop from 'S' to 'Z'
            Label letterLabel = createLetterLabel(c); // Assuming you have a method that creates the label
            letterPanelS_Z.add(letterLabel, c - 'S', 0); // Position labels in each column
        }

        // Add the GridPane to the StackPane
        letterPane3.getChildren().add(letterPanelS_Z);

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
        inputField.setLayoutY(450);
        inputField.setPrefWidth(50);
        inputField.setPrefHeight(50);

        // Once the window is displayed, ensure focus on the input field
        Platform.runLater(() -> inputField.requestFocus());

        // UPDATED
        // Restrict input to a single letter, auto-uppercase, and replace old value
        inputField.setOnKeyTyped(event -> {
            String input = inputField.getText().toUpperCase(); // Convert the entire text to uppercase

            if (!input.matches("[a-zA-Z]")) { // Allow only a single uppercase letter
                inputField.clear(); // Clear invalid input
                return;
            }

            inputField.setText(input); // Set uppercase text

            inputField.positionCaret(1);
            event.consume(); // Consume the event to prevent further processing
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
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\default_icon.png");
        Image hoverIcon = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\hover_icon.png");
        Image clickedIcon = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\clicked_icon.png");

        // Enter button with different state icons

        enterButton = new Button();
        ImageView imageViewDefault = new ImageView(defaultIcon);
        ImageView imageViewHover = new ImageView(hoverIcon);
        ImageView imageViewClicked = new ImageView(clickedIcon);

        enterButton.setPrefHeight(50); // Set height of the button

        // Set the image to adjust its aspect ratio while maintaining the height
        imageViewDefault.setFitHeight(50); // Set the height of the ImageView to 50
        imageViewDefault.setPreserveRatio(true); // Keep the aspect ratio of the image

        imageViewHover.setFitHeight(50); // Set the height of the ImageView to 50
        imageViewHover.setPreserveRatio(true);

        imageViewClicked.setFitHeight(50); // Set the height of the ImageView to 50 -fx-border-color: transparent;
        imageViewClicked.setPreserveRatio(true);

        enterButton.setGraphic(imageViewDefault);
        enterButton.setLayoutX(300);
        enterButton.setLayoutY(445);
        enterButton.setStyle("-fx-background-color: transparent;");
        enterButton.setOnMouseEntered(event -> enterButton.setGraphic(imageViewHover));
        enterButton.setOnMouseExited(event -> enterButton.setGraphic(imageViewDefault));
        enterButton.setOnMousePressed(event -> enterButton.setGraphic(imageViewClicked));
        enterButton.setOnMouseReleased(event -> enterButton.setGraphic(imageViewDefault));
        enterButton.setOnAction(event -> handleEnterButtonAction(enterButton));
        root.getChildren().add(enterButton);

        Image imgReturn = new Image(Constants.HOME);
        Image imgReturnHover = new Image(Constants.HOME_HOVER);
        Image imgReturnClick = new Image(Constants.HOME_CLICK);

        Button returnButton = createImageButton(imgReturn, imgReturnHover, imgReturnClick,
                5, 10, 30, 30, Constants.CLICK,
                event -> {
                    bgm.stopMusic();
                    GUI.fadeToLobby(primaryStage);
                    // GUI gui = new GUI();
                    // gui.start(new Stage());
                    // primaryStage.close();
                });

        root.getChildren().add(returnButton);

        Image imgMusic = new Image(Constants.IMG_MUSIC_BLACK);
        Image imgMusicOff = new Image(Constants.IMG_MUSIC_OFF);
        Image imgMusicClick = new Image(Constants.IMG_MUSIC_CLICK_GRAY);

        Button buttonMusic = createMusicButton(imgMusic, imgMusicClick, imgMusicOff, 5, 50, 30, 30, Constants.CLICK,
                event -> {
                    if (isMusicOn) {
                        bgm.resumeMusic();
                    } else {
                        bgm.pauseMusic();
                    }
                });

        root.getChildren().add(buttonMusic);
    }

    private Label createLetterLabel(char c) {
        Image originalImage = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\" + c + "_default.png");

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
                imagePath = "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\"
                        + guessedLetter
                        + "_correct.png";
            } else {
                // Incorrect guess
                imagePath = "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\"
                        + guessedLetter
                        + "_incorrect.png";
                incorrectGuesses++;

                // Call the updated scale method for hangman image
                scaleHangmanImage(incorrectGuesses); // Scale based on number of incorrect guesses

                if (incorrectGuesses >= 5) {
                    // Start playing the impending doom sound in a loop
                    impendingDoomPlayer.playMusic(Constants.IMPEND);
                }

                if (incorrectGuesses >= 6) {
                    impendingDoomPlayer.stopMusic(); // Stop impending doom music
                    musicPlayer.playSoundEffect(Constants.GAMEOVER);
                    bgm.stopMusic();
                    System.out.println("Score before result screen: " + score);
                    showResultScreen(score, false); // Game over
                    return;
                } else if (incorrectGuesses >= 5) {
                    impendingDoomPlayer.playMusic(Constants.IMPEND);
                }
            }

            // Load and scale the icon
            // Image originalImage = new Image(imagePath);
            Image scaledImage = new Image(imagePath, 50, 50, true, true);

            // Set the scaled icon to the corresponding letter
            letterLabels.get(guessedLetter).setGraphic(new ImageView(scaledImage));
            updateHiddenWord(guessedLetter);

            if (!hiddenWordLabel.getText().contains("ˍ")) {
                timer1.stop();
                if (secondsT == 1) {
                    score += 1;
                }

                if (category.equals("easy") && secondsT <= 45) {
                    score = score + ((45 - secondsT) / 2);
                } else if (category.equals("average") && secondsT <= 60) {
                    score = score + (60 - secondsT);
                } else if (category.equals("difficult") && secondsT <= 75) {
                    score = score + (75 - secondsT);
                }

                scoreValue.setText(String.valueOf(score));

                if (score > highScore.showHighScore(category)) { // Compare against the latest high score
                    highScore.updateHighScore(score, category); // Update the high score in the file
                    int updatedHighScore = highScore.showHighScore(category); // Get the latest score after updating
                    highScoreLabel.setText("High Score: " + String.valueOf(updatedHighScore));
                }

                resetGame(enterButton);
            }
        }
    }

    private void showResultScreen(int finalScore, boolean isWin) {

        System.out.println("Final Score received in showResultScreen: " + finalScore);
        StackPane resultRoot = new StackPane();
        Scene resultScene = new Scene(resultRoot, CommonConstants.FRAME_SIZE.width, CommonConstants.FRAME_SIZE.height);

        // Background Image
        Image backgroundImage = new Image(
                "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Result_Screen.png");
        System.out.println("Image loaded: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight()); // Debugging

        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        resultRoot.setBackground(new Background(background));

        Label wordRevealLabel = new Label();

        wordRevealLabel.setText("The word was: " + wordChallenge[1]);
        wordRevealLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        wordRevealLabel.setTextFill(Color.RED);
        wordRevealLabel.setAlignment(Pos.CENTER);
        StackPane.setMargin(wordRevealLabel, new Insets(400, 0, 0, 0));

        // Score Display
        Label scoreTextLabel = new Label("Score: ");
        scoreTextLabel.setStyle("-fx-font-size: 24px;");
        scoreTextLabel.setTextFill(Color.BLACK);
        scoreTextLabel.setAlignment(Pos.CENTER);
        StackPane.setMargin(scoreTextLabel, new Insets(0, 0, 150, 50));

        Label scoreValueLabel = new Label();
        scoreValueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        scoreValueLabel.setTextFill(Color.BLACK);
        scoreValueLabel.setAlignment(Pos.CENTER);
        StackPane.setMargin(scoreValueLabel, new Insets(0, 0, 150, 150));

        Platform.runLater(() -> scoreValueLabel.setText(String.valueOf(finalScore)));

        // Images
        Image imgPlayAgain = new Image(Constants.PLAYAGAIN);
        Image imgPlayAgainHover = new Image(Constants.PLAYAGAIN_HOVER);
        Image imgPlayAgainClick = new Image(Constants.PLAYAGAIN_CLICK);

        Button playAgainButton = createImageButton(imgPlayAgain, imgPlayAgainHover, imgPlayAgainClick,
                250, 100, 250, 60, Constants.CLICK,
                event -> {
                    primaryStage.close();
                    Hangman hangman = new Hangman(category);
                    Stage newStage = new Stage();
                    hangman.start(newStage);
                });

        Image imgReturn = new Image(Constants.RETURN);
        Image imgReturnHover = new Image(Constants.RETURN_HOVER);
        Image imgReturnClick = new Image(Constants.RETURN_CLICK);

        Button returnButton = createImageButton(imgReturn, imgReturnHover, imgReturnClick,
                250, 100, 250, 60, Constants.CLICK,
                event -> {
                    GUI.fadeToLobby(primaryStage);
                    // GUI gui = new GUI();
                    // gui.start(new Stage());
                    // primaryStage.close();
                });

        // Layout
        VBox layout = new VBox(15, playAgainButton, returnButton);
        layout.setAlignment(Pos.CENTER);

        // Shift downward
        // StackPane root = new StackPane(layout);
        StackPane.setMargin(layout, new Insets(145, 0, 0, 0)); // Adjust downward// Move it slightly downward

        resultRoot.getChildren().add(layout);

        resultRoot.getChildren().add(scoreTextLabel);
        resultRoot.getChildren().add(scoreValueLabel);

        resultRoot.getChildren().add(wordRevealLabel);

        // Show Scene
        primaryStage.setScene(resultScene);
        primaryStage.show();
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

    private void resetGame(Button enterButton) {
        // Reload the word challenge

        wordChallenge = wordDB.loadChallenge(category);
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
                    "file:D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources1\\Letters\\" + k
                            + "_default.png");
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

        Hangman hangman = new Hangman(category);
        hangman.start(new Stage());

        primaryStage.close(); // Close the current JavaFX stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}