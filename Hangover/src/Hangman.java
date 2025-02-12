import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class Hangman extends JFrame implements ActionListener {
    private int incorrectGuesses;
    private int score = 0;
    private String[] wordChallenge;
    private final WordDB wordDB;
    private JLabel hangmanImage, categoryLabel, hiddenWordLabel, resultLabel, backgroundImage;
    private JTextField inputField;
    private JButton enterButton;
    private HashMap<Character, JLabel> letterLabels;
    private static final String BASE_PATH = "resources/letters/";

    public Hangman() {
        super("Hangman Game");
        setSize(CommonConstants.FRAME_SIZE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);
        setUndecorated(true);

        wordDB = new WordDB();
        wordChallenge = wordDB.loadChallenge();
        letterLabels = new HashMap<>();
        addGuiComponents();
    }

    private void addGuiComponents() {
        // Hangman image
        hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
        hangmanImage.setBounds(650, 10, 300, 600);
        getContentPane().add(hangmanImage);

        scaleHangmanImage(incorrectGuesses);

        // Category label
        categoryLabel = new JLabel("Category: " + wordChallenge[0]);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 24));
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setBounds(50, 40, 400, 30);
        getContentPane().add(categoryLabel);

        // Hidden word label
        hiddenWordLabel = new JLabel(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setFont(new Font("Arial", Font.BOLD, 40));
        hiddenWordLabel.setForeground(Color.WHITE);
        hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hiddenWordLabel.setBounds(50, 100, 500, 50);
        getContentPane().add(hiddenWordLabel);

        // Letter panel for displaying letter icons A - I
        JPanel letterPanelA_I = new JPanel(new GridLayout(1, 10, 0, 0));
        letterPanelA_I.setBounds(50, 0, 500, 500);
        letterPanelA_I.setOpaque(false);

        // Loop through each letter from A to I
        for (char c = 'A'; c <= 'J'; c++) {
            // Load and scale the image
            ImageIcon originalIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + c + "_default.png");

            // Scale the image to fit the JLabel size
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize to fit
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            // Create a JLabel with the scaled icon
            JLabel letterLabel = new JLabel(scaledIcon);
            letterLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Store the label in the map
            letterLabels.put(c, letterLabel);

            // Add the label to the panel
            letterPanelA_I.add(letterLabel);
        }
        getContentPane().add(letterPanelA_I);

        // Letter panel for displaying letter icons J - S
        JPanel letterPanelJ_S = new JPanel(new GridLayout(1, 10, 0, 0));
        letterPanelJ_S.setBounds(50, 40, 500, 500);
        letterPanelJ_S.setOpaque(false);

        // Loop through each letter from J to S
        for (char c = 'K'; c <= 'T'; c++) {
            // Load and scale the image
            ImageIcon originalIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + c + "_default.png");

            // Scale the image to fit the JLabel size
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize to fit
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            // Create a JLabel with the scaled icon
            JLabel letterLabel = new JLabel(scaledIcon);
            letterLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Store the label in the map
            letterLabels.put(c, letterLabel);

            // Add the label to the panel
            letterPanelJ_S.add(letterLabel);
        }
        getContentPane().add(letterPanelJ_S);

        // Letter panel for displaying letter icons T - Z
        JPanel letterPanelT_Z = new JPanel(new GridLayout(1, 10, 0, 0));
        letterPanelT_Z.setBounds(50, 80, 500, 500);
        letterPanelT_Z.setOpaque(false);

        // Loop through each letter from T to Z
        for (char c = 'U'; c <= 'Z'; c++) {
            // Load and scale the image
            ImageIcon originalIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + c + "_default.png");

            // Scale the image to fit the JLabel size
            Image img = originalIcon.getImage();
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize to fit
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            // Create a JLabel with the scaled icon
            JLabel letterLabel = new JLabel(scaledIcon);
            letterLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Store the label in the map
            letterLabels.put(c, letterLabel);

            // Add the label to the panel
            letterPanelT_Z.add(letterLabel);
        }
        getContentPane().add(letterPanelT_Z);

        // Input field for letter guesses
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 24));
        inputField.setBounds(250, 400, 50, 40);
        
        inputField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e){
                char c = e.getKeyChar();
                if (!Character.isLetter(c) || inputField.getText().length() >= 1){
                    e.consume();
                }
            }
        });

        getContentPane().add(inputField);
        
        ImageIcon defaultIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\default_icon.png");
        ImageIcon hoverIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\hover_icon.png");
        ImageIcon clickedIcon = new ImageIcon("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\clicked_icon.png");

        // Enter button with different state icons
        enterButton = new JButton(defaultIcon);
        enterButton.setBounds(350, 400, 100, 40);
        enterButton.setRolloverIcon(hoverIcon);  // Image when hovered
        enterButton.setPressedIcon(clickedIcon); // Image when clicked
        enterButton.setFocusPainted(false);      // Remove focus border
        enterButton.setBorderPainted(false);     // Remove border
        enterButton.setContentAreaFilled(false); // Make the background transparent
        enterButton.addActionListener(this);
        getContentPane().add(enterButton);
    }
    
    private void scaleHangmanImage(int incorrectGuesses) {
        // Get the size of the container (window size)
        int panelWidth = getWidth() - 50;  // Add padding if necessary
        int panelHeight = getHeight() - 100; // Adjust to leave space for other components

        // Define max width and height constraints
        int maxWidth = 500;
        int maxHeight = 1000;

        // Make sure the width and height don't exceed the constraints
        if (panelWidth > maxWidth) panelWidth = maxWidth;
        if (panelHeight > maxHeight) panelHeight = maxHeight;

        // Get the image corresponding to the current incorrect guess
        String imagePath = "D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\src\\resources\\" + (incorrectGuesses + 1) + ".png";
        ImageIcon originalIcon = new ImageIcon(imagePath);

        // Get the original width and height of the image
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Calculate scaling factor to maintain aspect ratio
        double aspectRatio = (double) originalWidth / originalHeight;

        if (panelWidth / aspectRatio <= panelHeight) {
            // Scale based on width
            panelHeight = (int) (panelWidth / aspectRatio);
        } else {
            // Scale based on height
            panelWidth = (int) (panelHeight * aspectRatio);
        }

        // Scale the image to fit the panel while maintaining aspect ratio
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);

        // Set the scaled image to the JLabel
        hangmanImage.setIcon(scaledIcon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterButton) {
            String input = inputField.getText().toUpperCase();
            if (input.length() == 1 && input.charAt(0) >= 'A' && input.charAt(0) <= 'Z') {
                char guessedLetter = input.charAt(0);
                inputField.setText("");  // Clear the input field

                // Load the image based on the correct or incorrect state
                String imagePath = "";
                ImageIcon originalIcon;

                if (wordChallenge[1].contains(String.valueOf(guessedLetter))) {
                    // Correct guess
                    imagePath = "D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + guessedLetter + "_correct.png";
                } else {
                    // Incorrect guess
                    imagePath = "D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + guessedLetter + "_incorrect.png";
                    incorrectGuesses++;

                    // Call the updated scale method for hangman image
                    scaleHangmanImage(incorrectGuesses);  // Scale based on number of incorrect guesses

                    if (incorrectGuesses >= 6) {
                        JOptionPane.showMessageDialog(this, "Game Over! The word was: " + wordChallenge[1]);
                        resetGame();
                        return;  // Exit early to avoid further processing after game over
                    }
                }

                // Load and scale the icon
                originalIcon = new ImageIcon(imagePath);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Scale to fit
                ImageIcon scaledIcon = new ImageIcon(scaledImg);

                // Set the scaled icon to the corresponding letter
                letterLabels.get(guessedLetter).setIcon(scaledIcon);
                updateHiddenWord(guessedLetter);

                if (!hiddenWordLabel.getText().contains("*")) {
                    JOptionPane.showMessageDialog(this, "You got it right!");
                    resetGame();
                }
            }
        }
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

    private void resetGame() {

        // Reload the word challenge
        wordChallenge = wordDB.loadChallenge();
        incorrectGuesses = 0;
    
        scaleHangmanImage(incorrectGuesses); // Apply scaling to the image
    
        // Reset the hidden word
        hiddenWordLabel.setText(CustomTools.hideWords(wordChallenge[1]));
    
        // Reset letter images (back to default)
        letterLabels.forEach((k, v) -> v.setIcon(scaleImage("D:\\125 Hangman\\CMSC-125-Hangover\\Hangover\\resources\\Letters\\" + k + "_default.png", 50, 50)));
    
        // Optionally, clear the input field
        inputField.setText("");
    }
    
    // Helper method to scale an image to a specific width and height
    private ImageIcon scaleImage(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image img = originalIcon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize to fit
        return new ImageIcon(scaledImg);
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Hangman::new);
    }
}
