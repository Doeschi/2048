package main;

import model.Block;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    private PImage background;
    private int windowWidth = 800;
    private int windowHeight = 950;

    private int imageXOffset = 85;
    private int imageYOffset = 235;
    private int gridLineOffset = 10;

    private int tiles = 4;
    private int gameFieldSize = 600;
    private int tileSize = gameFieldSize / tiles;

    private Block[][] gameField = new Block[tiles][tiles];
    private Block[][] oldGameField = new Block[tiles][tiles];
    private List<Block> blocks = new ArrayList<>();
    private List<Block> oldBlocks = new ArrayList<>();
    private boolean blocksMoving = false;


    public void settings() {
        size(windowWidth, windowHeight);
    }

    public void setup() {
        frameRate(30);
        background = loadImage("background.png");
        for (int i = 0; i < 2; i++) {
            spawnNewBlock();
        }
    }

    public void draw() {
        background(255);
        drawGrid();
        translate(tileSize / 2, tileSize / 2);
        drawBlocks();
        translate(0, 0);
    }

    public void keyPressed() {
        if (!blocksMoving) {
            if (key == 'r' || keyCode == 'R') {
                gameField = oldGameField;
                blocks = oldBlocks;
            } else if (keyCode == LEFT) {
                startMove();
                // TODO: Mit Bildern arbeiten

                moveLeft();
                endMove();
            } else if (keyCode == UP) {
                startMove();
                moveUp();
                endMove();
            } else if (keyCode == RIGHT) {
                startMove();
                moveRight();
                endMove();
            } else if (keyCode == DOWN) {
                startMove();
                moveDown();
                endMove();
            }
        }
    }

    private void startMove() {
        blocksMoving = true;
        saveGameState();
    }

    private void endMove() {
        resetUnmergedProperty();
        if (gameFieldHasChanged()) {
            spawnNewBlock();
        }
    }

    private void saveGameState() {
        oldBlocks = new ArrayList<>();
        oldGameField = new Block[tiles][tiles];
        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if (gameField[yCord][xCord] != null) {
                    Block currentBlock = gameField[yCord][xCord];
                    Block newBlock = new Block(currentBlock.getValue(), currentBlock.getPosition(), currentBlock.isUnmerged(), currentBlock.getColor());
                    oldGameField[yCord][xCord] = newBlock;
                    oldBlocks.add(newBlock);
                } else {
                    oldGameField[yCord][xCord] = null;
                }
            }
        }
    }

    private boolean gameFieldHasChanged() {
        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if ((oldGameField[yCord][xCord] == null && gameField[yCord][xCord] != null) || (oldGameField[yCord][xCord] != null && gameField[yCord][xCord] == null)) {
                    return true;
                } else if (oldGameField[yCord][xCord] != null && gameField[yCord][xCord] != null) {
                    if (oldGameField[yCord][xCord].getValue() != gameField[yCord][xCord].getValue()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Draws a little grid
     */
    private void drawGrid() {
        for (int i = 0; i < tiles; i++) {
            int xCord = i * tileSize;
            line(xCord, 0, xCord, gameFieldSize);
            stroke(170);
        }

        for (int i = 0; i < tiles; i++) {
            int yCord = i * tileSize;
            line(0, yCord, gameFieldSize, yCord);
            stroke(170);
        }
    }

    /**
     * Creates a new block and makes sure that it is not placed above another block.
     */
    private void spawnNewBlock() {
        Random random = new Random();
        do {
            boolean freeCordinates = true;
            int xCord = (random.nextInt(tiles) * tileSize);
            int yCord = (random.nextInt(tiles) * tileSize);
            for (Block block : blocks) {
                if (block.getPosition().x == xCord && block.getPosition().y == yCord) {
                    freeCordinates = false;
                }
            }

            if (freeCordinates) {
                Block block = new Block(new PVector(xCord, yCord));
                blocks.add(block);
                gameField[(yCord / tileSize)][xCord / tileSize] = block;
                break;
            }
        } while (true);

    }

    /**
     * Draws every block
     */
    private void drawBlocks() {
        for (Block block : blocks) {
            //fill(235, 116, 12);
            fill(block.getColor().getRed(), block.getColor().getGreen(), block.getColor().getBlue());
            rectMode(CENTER);
            rect(block.getPosition().x, block.getPosition().y, tileSize, tileSize);
            noFill();
            drawNumber(block);
        }
    }

    /**
     * Draws the number of a block
     *
     * @param block the block
     */
    private void drawNumber(Block block) {
        fill(0);
        textSize(20);
        textAlign(CENTER, CENTER);
        text(block.getValue(), block.getPosition().x, block.getPosition().y);
        noFill();
    }

    private void moveLeft() {
        for (int yPos = 0; yPos < tiles; yPos++) {
            int xRow = 0;
            for (int xPos = 0; xPos < tiles; xPos++) {
                if (gameField[yPos][xPos] != null) {
                    if (xRow == 0) {
                        Block currentBlock = gameField[yPos][xPos];
                        gameField[yPos][xPos] = null;
                        gameField[yPos][xRow] = currentBlock;
                        currentBlock.setPosition(new PVector(xRow * tileSize, yPos * tileSize));
                        xRow++;
                    } else {
                        if (gameField[yPos][xRow - 1].getValue() == gameField[yPos][xPos].getValue() && gameField[yPos][xRow - 1].isUnmerged()) {
                            gameField[yPos][xRow - 1].setValue(gameField[yPos][xRow - 1].getValue() * 2);
                            gameField[yPos][xRow - 1].setUnmerged(false);
                            blocks.remove(gameField[yPos][xPos]);
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yPos][xRow] = currentBlock;
                            currentBlock.setPosition(new PVector(xRow * tileSize, yPos * tileSize));
                            xRow++;
                        }
                    }
                }
            }
        }
        blocksMoving = false;
    }

    private void moveUp() {
        for (int xPos = 0; xPos < tiles; xPos++) {
            int yRow = 0;
            for (int yPos = 0; yPos < tiles; yPos++) {
                if (gameField[yPos][xPos] != null) {
                    if (yRow == 0) {
                        Block currentBlock = gameField[yPos][xPos];
                        gameField[yPos][xPos] = null;
                        gameField[yRow][xPos] = currentBlock;
                        currentBlock.setPosition(new PVector(xPos * tileSize, yRow * tileSize));
                        yRow++;
                    } else {
                        if (gameField[yRow - 1][xPos].getValue() == gameField[yPos][xPos].getValue()) {
                            gameField[yRow - 1][xPos].setValue(gameField[yRow - 1][xPos].getValue() * 2);
                            blocks.remove(gameField[yPos][xPos]);
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yRow][xPos] = currentBlock;
                            currentBlock.setPosition(new PVector(xPos * tileSize, yRow * tileSize));
                            yRow++;
                        }
                    }
                }
            }
        }

        blocksMoving = false;
    }

    private void moveRight() {
        for (int yPos = 0; yPos < tiles; yPos++) {
            int xRow = tiles - 1;
            for (int xPos = tiles - 1; 0 <= xPos; xPos--) {
                if (gameField[yPos][xPos] != null) {
                    if (xRow == tiles - 1) {
                        Block currentBlock = gameField[yPos][xPos];
                        gameField[yPos][xPos] = null;
                        gameField[yPos][xRow] = currentBlock;
                        currentBlock.setPosition(new PVector(xRow * tileSize, yPos * tileSize));
                        xRow--;
                    } else {
                        if (gameField[yPos][xRow + 1].getValue() == gameField[yPos][xPos].getValue()) {
                            gameField[yPos][xRow + 1].setValue(gameField[yPos][xRow + 1].getValue() * 2);
                            blocks.remove(gameField[yPos][xPos]);
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yPos][xRow] = currentBlock;
                            currentBlock.setPosition(new PVector(xRow * tileSize, yPos * tileSize));
                            xRow--;
                        }
                    }
                }
            }
        }
        blocksMoving = false;
    }

    private void moveDown() {
        for (int xPos = 0; xPos < tiles; xPos++) {
            int yRow = tiles - 1;
            for (int yPos = tiles - 1; 0 <= yPos; yPos--) {
                if (gameField[yPos][xPos] != null) {
                    if (yRow == tiles - 1) {
                        Block currentBlock = gameField[yPos][xPos];
                        gameField[yPos][xPos] = null;
                        gameField[yRow][xPos] = currentBlock;
                        currentBlock.setPosition(new PVector(xPos * tileSize, yRow * tileSize));
                        yRow--;
                    } else {
                        if (gameField[yRow + 1][xPos].getValue() == gameField[yPos][xPos].getValue()) {
                            gameField[yRow + 1][xPos].setValue(gameField[yRow + 1][xPos].getValue() * 2);
                            blocks.remove(gameField[yPos][xPos]);
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yRow][xPos] = currentBlock;
                            currentBlock.setPosition(new PVector(xPos * tileSize, yRow * tileSize));
                            yRow--;
                        }
                    }
                }
            }
        }
        blocksMoving = false;
    }

    private void resetUnmergedProperty() {
        for (Block block : blocks) {
            block.setUnmerged(true);
        }
    }

    public static void main(String[] args) {
        String[] processingArgs = {"ProcessingTest"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}