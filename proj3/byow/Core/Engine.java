package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class Engine {
    private TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;
    private static final int REPLAY_STEP_DELAY = 200;
    private static final int FONT_SIZE = 15;
    private static final double X_SCALE = 0.2;
    private static final double Y_SCALE = 0.97;
    private boolean gameOver = false;
    private Handler handler;
    private File file = new File("game.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        String input = "";
        ter.initialize(WIDTH, HEIGHT);
        handleMenu();
        boolean gameStarted = false;
        boolean quitSequenceStarted = false;
        boolean userQuit = false;
        while (!gameStarted) {
            if (StdDraw.hasNextKeyTyped()) {
                char startChar = StdDraw.nextKeyTyped();
                if (startChar == 'q' || startChar == 'Q') {
                    userQuit = true;
                    break;
                } else {
                    if (startChar == 'n' || startChar == 'N') {
                        eraseGameData();
                        input += "" + startChar;
                        boolean seedCompleted = false;
                        askForSeed();
                        while (!seedCompleted) {
                            if (StdDraw.hasNextKeyTyped()) {
                                char seedChar = StdDraw.nextKeyTyped();
                                input += "" + seedChar;
                                if (!input.contains("s") && !input.contains("S")) {
                                    showSeed(input.substring(1, input.length()));
                                }
                            }
                            seedCompleted = input.contains("s") || input.contains("S");
                        }
                        handler = new Handler(WIDTH, HEIGHT, Long.parseLong(input.substring(1, input.length() - 1)));
                    } else {
                        if (file.exists()) {
                            input = handleSaveLoad(startChar);
                            eraseGameData();
                        }
                    }
                    gameStarted = true;
                }
            }
        }
        if (handler != null) {
            ter.renderFrame(handler.getWorld());
            while (true) {
                showHUD();
                ter.renderFrame(handler.getWorld());
                if (StdDraw.hasNextKeyTyped()) {
                    char move = StdDraw.nextKeyTyped();
                    if ((move == 'Q' || move == 'q') && quitSequenceStarted) {
                        break;
                    } else {
                        quitSequenceStarted = false;
                    }
                    if (move == ':') {
                        quitSequenceStarted = true;
                        continue;
                    }
                    input += "" + move;
                    handler.handleHeroMovement(move);
                    ter.renderFrame(handler.getWorld());
                }
            }
            saveGameData(input);
        }
        handleEnd();
    }

    public void setFormat() {
        StdDraw.clear(Color.BLUE);
        StdDraw.setPenColor(Color.ORANGE);
        Font font = new Font("Times New Roman", Font.BOLD, FONT_SIZE);
        StdDraw.setFont(font);
    }

    public void saveGameData(String input) {
        /**
         * @source https://docs.oracle.com/javase/tutorial/essential/io/file.html
         * Used code from the tutorial as a basis for writing code to write data into a file
         */
        file = new File("game.txt");
        Path path = file.toPath();
        try {
            Files.createFile(path);
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
        }
        byte[] data = input.getBytes();
        try (OutputStream stream = new BufferedOutputStream(Files.newOutputStream(path))) {
            file.setWritable(true);
            stream.write(data, 0, data.length);
        } catch (IOException e) {
            System.out.println(e.fillInStackTrace());
        }
    }

    public void eraseGameData() {
        file = new File("game.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    public void handleMenu() {
        setFormat();
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Welcome to the game. Press 'N' to load a new world. "
                + "Press 'L' to load your last save. Press 'R' to replay your last save. "
                + "Press 'Q' to quit.");
        StdDraw.show();
    }

    public void showHUD() {
        int tileX = (int) StdDraw.mouseX();
        int tileY = (int) StdDraw.mouseY();
        String time = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        time = time.substring(0, time.length() - 6);
        System.out.println();
        TETile tile = handler.getWorld()[tileX][tileY];
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH * X_SCALE, HEIGHT * Y_SCALE, tile.description());
        StdDraw.text(WIDTH * (1 - X_SCALE), HEIGHT * Y_SCALE, time);
        StdDraw.show();
    }

    public void handleEnd() {
        setFormat();
        if (handler == null) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "You quit the game.");
        } else if (handler.isGameOver()) {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "You lose.");
        } else {
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Game Saved.");
        }
        StdDraw.show();
    }

    public String handleSaveLoad(char startChar) {
        In in = new In("game.txt");
        String gameData = "";
        if (!in.isEmpty()) {
            gameData = in.readString().toLowerCase();
            this.handler = handleWorldCreation(gameData);
            String commandSequence = gameData.substring(gameData.indexOf("s") + 1, gameData.length());
            if (startChar == 'l' || startChar == 'L') {
                enactCommandSequence(commandSequence, false, false);
            } else if (startChar == 'r' || startChar == 'R') {
                enactCommandSequence(commandSequence, true, false);
            }
        }
        return gameData;
    }

    public Handler handleWorldCreation(String input) {
        input = input.toLowerCase();
        String stringSeed = input.substring(1, input.indexOf("s"));
        long seed = Long.parseLong(stringSeed);
        return new Handler(WIDTH, HEIGHT, seed);
    }

    public void askForSeed() {
        setFormat();
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Please enter a seed in the format: 'A random digit"
                + " sequence followed by 'S'");
        StdDraw.show();
    }

    public void showSeed(String seed) {
        setFormat();
        StdDraw.text(WIDTH / 2, HEIGHT / 2, seed);
        StdDraw.show();
    }

    public TETile[][] interactWithInputString(String input) {
        input = input.toLowerCase();
        ter.initialize(WIDTH, HEIGHT);
        if (input.charAt(0) == 'n') {
            eraseGameData();
            handler = new Handler(WIDTH, HEIGHT, Long.parseLong(input.substring(1, input.indexOf("s"))));
            enactCommandSequence(input.substring(input.indexOf("s") + 1, input.length()), false, true);
            if (input.contains(":q")) {
                saveGameData(input.substring(0, input.length() - 2));
            } else {
                saveGameData(input);
            }
        } else {
            if (file.exists()) {
                In in = new In("game.txt");
                String newCommands = "";
                if (!in.isEmpty()) {
                    String gameData = in.readString().toLowerCase();
                    this.handler = handleWorldCreation(gameData);
                    String commandSequence = gameData.substring(gameData.indexOf("s") + 1, gameData.length());
                    if (input.charAt(0) == 'l') {
                        enactCommandSequence(commandSequence, false, true);
                        eraseGameData();
                        newCommands = enactCommandSequence(input.substring(1, input.length()), false, true);
                    } else if (input.charAt(0) == 'r') {
                        enactCommandSequence(commandSequence, true, true);
                        eraseGameData();
                        newCommands = enactCommandSequence(input.substring(1, input.length()), true, true);
                    }
                    saveGameData(gameData + newCommands);
                }
            }
        }
        return handler.getWorld();
    }

    public String enactCommandSequence(String commandSequence, boolean isReplay, boolean isInputStringCase) {
        boolean quitSequenceStarted = false;
        String newCommands = "";
        int i = 0;
        if (isInputStringCase) {
            ter.renderFrame(handler.getWorld());
        }
        while (i < commandSequence.length()) {
            if (commandSequence.charAt(i) == 'q' && quitSequenceStarted) {
                break;
            } else if (commandSequence.charAt(i) == ':') {
                quitSequenceStarted = true;
            } else {
                newCommands += commandSequence.charAt(i);
            }
            handler.handleHeroMovement(commandSequence.charAt(i));
            if (isReplay || isInputStringCase) {
                StdDraw.pause(REPLAY_STEP_DELAY);
                ter.renderFrame(handler.getWorld());
            }
            i++;
        }
        return newCommands;
    }
}
