import java.io.*;
import java.net.URL;
import java.util.*;

public class WordDB {
    // will contain key -> category, value -> words
    private HashMap<String, String[]> wordList;
    private ArrayList<String> categories; // used to pick random categories
    private static final String DATA_PATH = "resources/data.txt"; // Adjust path if needed

    public WordDB() {
        try {
            wordList = new HashMap<>();
            categories = new ArrayList<>();

            // Load the file from inside the JAR/EXE
            InputStream input = getClass().getClassLoader().getResourceAsStream(DATA_PATH);
            if (input == null) {
                throw new RuntimeException("ERROR: data.txt NOT FOUND!");
            }

            // Debugging: Check if file exists
            URL resourceUrl = getClass().getClassLoader().getResource(DATA_PATH);
            System.out.println("File URL: " + (resourceUrl == null ? "NOT FOUND" : resourceUrl));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String category = parts[0];
                    categories.add(category);

                    String[] values = Arrays.copyOfRange(parts, 1, parts.length);
                    wordList.put(category, values);
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public String[] loadChallenge(String chosenCategory) {
        Random rand = new Random();
        String category = chosenCategory;

        String[] categoryValues = wordList.get(category);
        if (categoryValues == null) {
            throw new IllegalArgumentException("Category not found: " + category);
        }
        String word = categoryValues[rand.nextInt(categoryValues.length)];

        return new String[] { category.toUpperCase(), word.toUpperCase() };
    }
}
