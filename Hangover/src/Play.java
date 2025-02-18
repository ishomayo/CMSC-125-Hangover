import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Play extends JFrame implements ActionListener {
    // counts the number of incorrect guesses player has made
    private int incorrectGuesses;

    // store the challenge from the WordDB here
    private String[] wordChallenge;

    private final WordDB wordDB = new WordDB();
    private JLabel hangmanImage, hiddenWordLabel, resultLabel, wordLabel;
    private JButton[] letterButtons;
    private JDialog resultDialog;
    private Font customFont;


    public Play(){
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);

        // init vars
        letterButtons = new JButton[26];
        wordChallenge = wordDB.loadChallenge();
        customFont = CustomTools.createFont(CommonConstants.FONT_PATH);
        createResultDialog();

        addGuiComponents();
    }

    private void addGuiComponents(){
        // hangman image
        hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
        hangmanImage.setBounds(300, 0, hangmanImage.getPreferredSize().width, hangmanImage.getPreferredSize().height);

        // category display
        // categoryLabel = new JLabel(wordChallenge[0]);
        // categoryLabel.setFont(customFont.deriveFont(30f));
        // categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // categoryLabel.setOpaque(true);
        // categoryLabel.setForeground(Color.WHITE);
        // categoryLabel.setBackground(CommonConstants.SECONDARY_COLOR);
        // categoryLabel.setBorder(BorderFactory.createLineBorder(CommonConstants.SECONDARY_COLOR));
        // categoryLabel.setBounds(
        //         0,
        //         hangmanImage.getPreferredSize().height - 28,
        //         CommonConstants.FRAME_SIZE.width,
        //         categoryLabel.getPreferredSize().height
        // );

        // hidden word
        hiddenWordLabel = new JLabel(CustomTools.hideWords(wordChallenge[1]));
        hiddenWordLabel.setFont(customFont.deriveFont(64f));
        hiddenWordLabel.setForeground(Color.WHITE);
        hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hiddenWordLabel.setBounds(
                250,
                150,
                CommonConstants.FRAME_SIZE.width,
                hiddenWordLabel.getPreferredSize().height
        );

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(25, 250, CommonConstants.BUTTON_PANEL_SIZE.width, CommonConstants.BUTTON_PANEL_SIZE.height);
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Better layout

        String[] rows = {
            "QWERTYUIOP",
            "ASDFGHJKL",
            "ZXCVBNM"
        };

        letterButtons = new JButton[26]; // Ensure the array has space for all letters

        for (String row : rows) {
            for (char c : row.toCharArray()) {
                JButton button = new JButton(Character.toString(c));
                button.setBackground(Color.WHITE);
                button.setFont((customFont != null) ? customFont.deriveFont(22f) : new Font("Arial", Font.PLAIN, 22));
                button.setForeground(Color.BLACK);
                button.addActionListener(this);

                int currentIndex = c - 'A';
                if (currentIndex >= 0 && currentIndex < letterButtons.length) {
                    letterButtons[currentIndex] = button;
                }

                buttonPanel.add(button); // No need for setBounds
            }
        }


        // letter buttons
        // GridLayout gridLayout = new GridLayout(3, 10);
        // JPanel buttonPanel = new JPanel();
        // buttonPanel.setBounds(
        //         50,
        //         250,
        //         CommonConstants.BUTTON_PANEL_SIZE.width,
        //         CommonConstants.BUTTON_PANEL_SIZE.height
        // );
        // buttonPanel.setLayout(null);
        // buttonPanel.setBackground(null);

        // customFont = new Font("Arial", Font.PLAIN, 22); // Default font if loading fails

        //         // create the letter buttons
        //         int x = 0, y = 0;
        //         int buttonWidth = 40, buttonHeight = 40;
        //         int spacing = 10;
        
        //         String[] rows = {
        //             "QWERTYUIOP",
        //             "ASDFGHJKL",
        //             "ZXCVBNM"
        //         };
        
        //         for (int row = 0; row < rows.length; row++) {
        //             for (int col = 0; col < rows[row].length(); col++) {
        //                 char c = rows[row].charAt(col);
        //                 JButton button = new JButton(Character.toString(c));
        //                 button.setBackground(Color.WHITE);
        //                 button.setFont(customFont);
        //                 button.setForeground(Color.BLACK);
        //                 button.addActionListener(this);
        
        //                 // using ASCII values to calculate the current index
        //                 int currentIndex = c - 'A';
        
        //                 letterButtons[currentIndex] = button;
        //                 button.setBounds(x, y, buttonWidth, buttonHeight);
        //                 buttonPanel.add(letterButtons[currentIndex]);
        
        //                 x += buttonWidth + spacing;
        //             }
        //             x = (row == 1) ? buttonWidth / 2 : 0; // Adjust x position for the next row
        //             y += buttonHeight + spacing;
        //         }

        // // create the letter buttons
        // for(char c = 'A'; c <= 'Z'; c++){
        //     JButton button = new JButton(Character.toString(c));
        //     button.setBackground(CommonConstants.PRIMARY_COLOR);
        //     button.setFont(customFont.deriveFont(22f));
        //     button.setForeground(Color.WHITE);
        //     button.addActionListener(this);

        //     // using ASCII values to caluclate the current index
        //     int currentIndex = c - 'A';

        //     letterButtons[currentIndex] = button;
        //     buttonPanel.add(letterButtons[currentIndex]);
        // }

        // reset button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(customFont.deriveFont(22f));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(CommonConstants.SECONDARY_COLOR);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);

        // quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(customFont.deriveFont(22f));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(CommonConstants.SECONDARY_COLOR);
        quitButton.addActionListener(this);
        buttonPanel.add(quitButton);

        // getContentPane().add(categoryLabel);
        getContentPane().add(hangmanImage);
        getContentPane().add(hiddenWordLabel);
        getContentPane().add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("Reset") || command.equals("Restart")){
            resetGame();

            if(command.equals("Restart")){
                resultDialog.setVisible(false);
            }
        }else if(command.equals("Quit")){
            dispose();
            return;
        }else{
            // letter buttons

            // disable button
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);

            // check if the word contains the user's guess,
            if(wordChallenge[1].contains(command)){
                // indicate that the user got it right
                button.setBackground(Color.GREEN);

                // store the hidden word in a char array, so update the hidden text
                char[] hiddenWord = hiddenWordLabel.getText().toCharArray();

                for(int i = 0; i < wordChallenge[1].length(); i++){
                    // update _ to correct letter
                    if(wordChallenge[1].charAt(i) == command.charAt(0)){
                        hiddenWord[i] = command.charAt(0);
                    }
                }

                // update hiddenWordLabel
                hiddenWordLabel.setText(String.valueOf(hiddenWord));

                // the user guessed the word right
                if(!hiddenWordLabel.getText().contains("_ ")){
                    // display dialog with success result
                    resultLabel.setText("You got it right!");
                    resultDialog.setVisible(true);
                }

            }else{
                // indicate that the user chose the wrong letter
                button.setBackground(Color.RED);

                // increase incorrect counter
                ++incorrectGuesses;

                // update hangman image
                CustomTools.updateImage(hangmanImage, "resources/" + (incorrectGuesses + 1) + ".png");

                // user failed to guess word right
                if(incorrectGuesses >= 6){
                    // display result dialog with game over label
                    resultLabel.setText("Too Bad, Try Again?");
                    resultDialog.setVisible(true);
                }
            }
            wordLabel.setText("Word: " + wordChallenge[1]);
        }

    }

    private void createResultDialog(){
        resultDialog = new JDialog();
        resultDialog.setTitle("Result");
        resultDialog.setSize(CommonConstants.RESULT_DIALOG_SIZE);
        resultDialog.getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);
        resultDialog.setResizable(false);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setModal(true);
        resultDialog.setLayout(new GridLayout(3, 1));
        resultDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetGame();
            }
        });

        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wordLabel = new JLabel();
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(CommonConstants.SECONDARY_COLOR);
        restartButton.addActionListener(this);

        resultDialog.add(resultLabel);
        resultDialog.add(wordLabel);
        resultDialog.add(restartButton);
    }

    private void resetGame(){
        // load new challenge
        wordChallenge = wordDB.loadChallenge();
        incorrectGuesses = 0;

        // load starting image
        CustomTools.updateImage(hangmanImage, CommonConstants.IMAGE_PATH);

        // update category
        // categoryLabel.setText(wordChallenge[0]);

        // update hiddenWord
        String hiddenWord = CustomTools.hideWords(wordChallenge[1]);
        hiddenWordLabel.setText(hiddenWord);

        // enable all buttons again
        for(int i = 0; i < letterButtons.length; i++){
            letterButtons[i].setEnabled(true);
            letterButtons[i].setBackground(CommonConstants.PRIMARY_COLOR);
        }
    }
}












