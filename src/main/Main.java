package main;

import model.Block;
import model.Direction;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    private Direction animationDirection = Direction.LEFT;
    private int animationSpeed = 25;

    private PImage backgroundImage;
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

    private boolean inAnimation = false;
    private boolean finishMove = false;

    // TODO: Merge von 2 Blöcken smoother machen
    // TODO: Mit Bildern arbeiten
    // TODO: Loosing Condition
    // TODO: Arrays in Matrix Objekte verwandeln

    // Processing Setup

    public void settings() {
        size(windowWidth, windowHeight);
    }

    public void setup() {
        frameRate(100);
        backgroundImage = loadImage("background.png");
        for (int i = 0; i < 2; i++) {
            spawnNewBlock();
        }
        saveGameState();
    }

    public void draw() {
        background(255);
        animateMovement();
        setInAnimation();
        drawGrid();
        translate(tileSize / 2, tileSize / 2);
        drawBlocks();
        translate(0, 0);

        if (!inAnimation && finishMove) {
            endMove();
        }
    }

    public void keyPressed() {
        if (!inAnimation) {
            if (key == 'r' || keyCode == 'R') {
                loadOldGameState();
            } else if (keyCode == LEFT) {
                animationDirection = Direction.LEFT;
                startMove();
                moveLeft();
                finishMove = true;
            } else if (keyCode == UP) {
                animationDirection = Direction.UP;
                startMove();
                moveUp();
                finishMove = true;
            } else if (keyCode == RIGHT) {
                animationDirection = Direction.RIGHT;
                startMove();
                moveRight();
                finishMove = true;
            } else if (keyCode == DOWN) {
                animationDirection = Direction.DOWN;
                startMove();
                moveDown();
                finishMove = true;
            }
        }
    }

    private void startMove() {
        saveGameState();
        for (Block block : blocks) {
            block.setInAnimation(true);
        }
    }

    private void endMove(){
        if (gameFieldHasChanged()) {
            spawnNewBlock();
        }
        updateValues();
        removeMergedBlocks();
        resetUnmergedProperty();
        finishMove = false;
    }

    private void animateMovement() {
        if (animationDirection == Direction.LEFT) {
            for (Block block : blocks) {
                if (block.getPosition().x > block.getNextPosition().x) {
                    block.setPosition(new PVector(block.getPosition().x - animationSpeed, block.getPosition().y));
                } else {
                    block.setInAnimation(false);
                }
            }

        } else if (animationDirection == Direction.UP) {
            for (Block block : blocks) {
                if (block.getPosition().y > block.getNextPosition().y) {
                    block.setPosition(new PVector(block.getPosition().x, block.getPosition().y - animationSpeed));
                } else {
                    block.setInAnimation(false);
                }
            }

        } else if (animationDirection == Direction.RIGHT) {
            for (Block block : blocks) {
                if (block.getPosition().x < block.getNextPosition().x) {
                    block.setPosition(new PVector(block.getPosition().x + animationSpeed, block.getPosition().y));
                } else {
                    block.setInAnimation(false);
                }
            }

        } else {
            for (Block block : blocks) {
                if (block.getPosition().y < block.getNextPosition().y) {
                    block.setPosition(new PVector(block.getPosition().x, block.getPosition().y + animationSpeed));
                } else {
                    block.setInAnimation(false);
                }
            }
        }
    }


    private void setInAnimation() {
        for (Block block : blocks) {
            if (block.isInAnimation()) {
                inAnimation = true;
                return;
            }
        }
        inAnimation = false;
    }

    private void saveGameState() {
        oldBlocks = new ArrayList<>();
        oldGameField = new Block[tiles][tiles];
        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if (gameField[yCord][xCord] != null) {
                    Block currentBlock = gameField[yCord][xCord];
                    Block newBlock = new Block(currentBlock.getValue(), currentBlock.getPosition(), currentBlock.getNextPosition(), currentBlock.isUnMerged(), currentBlock.getColor());
                    oldGameField[yCord][xCord] = newBlock;
                    oldBlocks.add(newBlock);
                } else {
                    oldGameField[yCord][xCord] = null;
                }
            }
        }
    }

    private void loadOldGameState() {
        gameField = oldGameField;
        blocks = oldBlocks;
    }

    private boolean gameFieldHasChanged() {
        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if ((oldGameField[yCord][xCord] == null && gameField[yCord][xCord] != null) || (oldGameField[yCord][xCord] != null && gameField[yCord][xCord] == null)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateValues() {
        for (Block block : blocks) {
            if (!block.isUnMerged()) {
                block.setValue(block.getValue() * 2);
            }
        }
    }

    private void removeMergedBlocks() {
        List<Block> copyOfBlocks = new ArrayList<>(blocks);
        for (Block block : copyOfBlocks) {
            if (block.isMergeRemove()) {
                blocks.remove(block);
            }
        }
    }


    private void resetUnmergedProperty() {
        for (Block block : blocks) {
            block.setUnMerged(true);
        }
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

    private void moveLeft() {
        for (int yPos = 0; yPos < tiles; yPos++) {
            int xRow = 0;
            for (int xPos = 0; xPos < tiles; xPos++) {
                if (gameField[yPos][xPos] != null) {
                    if (xRow == 0) {
                        Block currentBlock = gameField[yPos][xPos];
                        gameField[yPos][xPos] = null;
                        gameField[yPos][xRow] = currentBlock;
                        currentBlock.setNextPosition(new PVector(xRow * tileSize, yPos * tileSize));
                        xRow++;
                    } else {
                        if (gameField[yPos][xRow - 1].getValue() == gameField[yPos][xPos].getValue() && gameField[yPos][xRow - 1].isUnMerged()) {
                            gameField[yPos][xRow - 1].setUnMerged(false);
                            gameField[yPos][xPos].setMergeRemove(true);
                            gameField[yPos][xPos].setNextPosition(gameField[yPos][xRow - 1].getNextPosition());
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yPos][xRow] = currentBlock;
                            currentBlock.setNextPosition(new PVector(xRow * tileSize, yPos * tileSize));
                            xRow++;
                        }
                    }
                }
            }
        }
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
                        currentBlock.setNextPosition(new PVector(xPos * tileSize, yRow * tileSize));
                        yRow++;
                    } else {
                        if (gameField[yRow - 1][xPos].getValue() == gameField[yPos][xPos].getValue() && gameField[yRow - 1][xPos].isUnMerged()) {
                            gameField[yRow - 1][xPos].setUnMerged(false);
                            gameField[yPos][xPos].setMergeRemove(true);
                            gameField[yPos][xPos].setNextPosition(gameField[yRow - 1][xPos].getNextPosition());
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yRow][xPos] = currentBlock;
                            currentBlock.setNextPosition(new PVector(xPos * tileSize, yRow * tileSize));
                            yRow++;
                        }
                    }
                }
            }
        }
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
                        currentBlock.setNextPosition(new PVector(xRow * tileSize, yPos * tileSize));
                        xRow--;
                    } else {
                        if (gameField[yPos][xRow + 1].getValue() == gameField[yPos][xPos].getValue() && gameField[yPos][xRow + 1].isUnMerged()) {
                            gameField[yPos][xRow + 1].setUnMerged(false);
                            gameField[yPos][xPos].setMergeRemove(true);
                            gameField[yPos][xPos].setNextPosition(gameField[yPos][xRow + 1].getNextPosition());
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yPos][xRow] = currentBlock;
                            currentBlock.setNextPosition(new PVector(xRow * tileSize, yPos * tileSize));
                            xRow--;
                        }
                    }
                }
            }
        }
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
                        currentBlock.setNextPosition(new PVector(xPos * tileSize, yRow * tileSize));
                        yRow--;
                    } else {
                        if (gameField[yRow + 1][xPos].getValue() == gameField[yPos][xPos].getValue() && gameField[yRow + 1][xPos].isUnMerged()) {
                            gameField[yRow + 1][xPos].setUnMerged(false);
                            gameField[yPos][xPos].setMergeRemove(true);
                            gameField[yPos][xPos].setNextPosition(gameField[yRow + 1][xPos].getNextPosition());
                            gameField[yPos][xPos] = null;
                        } else {
                            Block currentBlock = gameField[yPos][xPos];
                            gameField[yPos][xPos] = null;
                            gameField[yRow][xPos] = currentBlock;
                            currentBlock.setNextPosition(new PVector(xPos * tileSize, yRow * tileSize));
                            yRow--;
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String[] processingArgs = {"ProcessingTest"};
        Main main = new Main();
        PApplet.runSketch(processingArgs, main);
    }
}