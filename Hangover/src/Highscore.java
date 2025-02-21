import java.io.*;
import java.nio.file.*;
import java.util.List;

class HighScore {
    private int number;

    public HighScore() {
    }

    public int showHighScore(String category) {
        try {
            Path path = Paths
                    .get("C:\\Users\\Eugene\\Desktop\\Git\\CMSC-125-Hangover\\Hangover\\src\\resources\\High Score "
                            + category + ".txt");

            System.out.println("Checking file: " + path);

            if (!Files.exists(path)) {
                System.out.println("File not found, returning 0.");
                return 0;
            }

            List<String> lines = Files.readAllLines(path);
            if (!lines.isEmpty()) {
                try {
                    number = Integer.parseInt(lines.get(0).trim().replaceAll("[^0-9]", ""));
                    return number;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid format! Resetting to 0.");
                    return 0;
                }
            } else {
                System.out.println("File is empty, returning 0.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateHighScore(int newHighScore, String category) {
        try {
            Path dir = Paths.get("C:\\Users\\Eugene\\Desktop\\Git\\CMSC-125-Hangover\\Hangover\\src\\resources");

            if (!Files.exists(dir)) {
                Files.createDirectories(dir); // Ensure directory exists
            }

            Path path = dir.resolve("High Score " + category + ".txt");

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            int currentHighScore = showHighScore(category);
            if (newHighScore > currentHighScore) {
                Files.write(path, String.valueOf(newHighScore).getBytes(), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("New high score saved: " + newHighScore);
            } else {
                System.out.println("Score is not higher than the current high score. No update.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
