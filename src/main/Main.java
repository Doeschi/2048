package main;

import model.Block;
import model.Direction;
import model.JColor;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends PApplet {

    private Direction animationDirection = Direction.LEFT;
    private int animationSpeed = 20;

    private int tiles = 4;
    private int gameFieldSize = 600;
    private int tileSize = gameFieldSize / tiles;

    private Block[][] gameField = new Block[tiles][tiles];
    private Block[][] oldGameField = new Block[tiles][tiles];
    private List<Block> blocks = new ArrayList<>();
    private List<Block> oldBlocks = new ArrayList<>();
    private int score = 0;

    private boolean inAnimation = false;
    private boolean finishMove = false;

    private PImage backgroundImage;
    private int windowWidth = 800;
    private int windowHeight = 950;

    private int imageXOffset = 85;
    private int imageYOffset = 235;
    private int gridLineOffset = 10;
    private int tileOffset = tileSize + gridLineOffset;
    private int scoreXCord = 610;
    private int scoreYCord = 135;

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
        animateMovement();
        setInAnimation();
        drawGame();

        if (!inAnimation && finishMove) {
            endMove();
        }

        if (!movePossible()) {
            drawGame();
            fill(JColor.GAMEOVER.getRed(), JColor.GAMEOVER.getGreen(), JColor.GAMEOVER.getBlue());
            textAlign(CENTER, BOTTOM);
            text("Gameover", windowWidth / 2, imageYOffset - 15);
            noLoop();
        }
    }

    public void keyPressed() {
        if (!inAnimation) {
            if (key == 'r' || keyCode == 'R') {
                loadOldGameState();
            } else if (key == 'g' || key == 'G') {
                restartGame();
                loop();
            } else if (key == 'a' || key == 'A' || keyCode == LEFT) {
                animationDirection = Direction.LEFT;
                startMove();
                moveLeft();
                finishMove = true;
            } else if (key == 'w' || key == 'W' || keyCode == UP) {
                animationDirection = Direction.UP;
                startMove();
                moveUp();
                finishMove = true;
            } else if (key == 'd' || key == 'D' || keyCode == RIGHT) {
                animationDirection = Direction.RIGHT;
                startMove();
                moveRight();
                finishMove = true;
            } else if (key == 's' || key == 'S' || keyCode == DOWN) {
                animationDirection = Direction.DOWN;
                startMove();
                moveDown();
                finishMove = true;
            }
        }
    }

    private void drawGame(){
        image(backgroundImage, 0, 0);
        drawScore();
        drawBlocks();
    }

    private void startMove() {
        saveGameState();
        for (Block block : blocks) {
            block.setInAnimation(true);
        }
    }

    private void endMove() {
        if (gameFieldHasChanged()) {
            spawnNewBlock();
        }
        updateValuesAndScore();
        removeMergedBlocks();
        resetUnmergedProperty();
        finishMove = false;
        drawGame();
    }

    private void restartGame() {
        animationDirection = Direction.LEFT;

        gameField = new Block[tiles][tiles];
        oldGameField = new Block[tiles][tiles];
        blocks = new ArrayList<>();
        oldBlocks = new ArrayList<>();
        score = 0;

        inAnimation = false;
        finishMove = false;

        for (int i = 0; i < 2; i++) {
            spawnNewBlock();
        }
    }

    private void animateMovement() {
        if (animationDirection == Direction.LEFT) {
            for (Block block : blocks) {
                if (block.getPosition().x - animationSpeed > block.getNextPosition().x) {
                    block.setPosition(new PVector(block.getPosition().x - animationSpeed, block.getPosition().y));
                } else {
                    block.setInAnimation(false);
                    block.setPosition(block.getNextPosition());
                }
            }

        } else if (animationDirection == Direction.UP) {
            for (Block block : blocks) {
                if (block.getPosition().y - animationSpeed > block.getNextPosition().y) {
                    block.setPosition(new PVector(block.getPosition().x, block.getPosition().y - animationSpeed));
                } else {
                    block.setInAnimation(false);
                    block.setPosition(block.getNextPosition());
                }
            }

        } else if (animationDirection == Direction.RIGHT) {
            for (Block block : blocks) {
                if (block.getPosition().x + animationSpeed < block.getNextPosition().x) {
                    block.setPosition(new PVector(block.getPosition().x + animationSpeed, block.getPosition().y));
                } else {
                    block.setInAnimation(false);
                    block.setPosition(block.getNextPosition());
                }
            }

        } else {
            for (Block block : blocks) {
                if (block.getPosition().y + animationSpeed < block.getNextPosition().y) {
                    block.setPosition(new PVector(block.getPosition().x, block.getPosition().y + animationSpeed));
                } else {
                    block.setInAnimation(false);
                    block.setPosition(block.getNextPosition());
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

    private boolean movePossible() {
        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if (gameField[yCord][xCord] == null) {
                    return true;
                }
            }
        }

        for (int yCord = 0; yCord < tiles; yCord++) {
            for (int xCord = 0; xCord < tiles; xCord++) {
                if (xCord > 0) {
                    if (gameField[yCord][xCord - 1].getValue() == gameField[yCord][xCord].getValue()) {
                        return true;
                    }
                }
                if (xCord < tiles - 1) {
                    if (gameField[yCord][xCord].getValue() == gameField[yCord][xCord + 1].getValue()) {
                        return true;
                    }
                }
                if (yCord > 0) {
                    if (gameField[yCord - 1][xCord].getValue() == gameField[yCord][xCord].getValue()) {
                        return true;
                    }
                }
                if (yCord < tiles - 1) {
                    if (gameField[yCord][xCord].getValue() == gameField[yCord + 1][xCord].getValue()) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private void updateValuesAndScore() {
        for (Block block : blocks) {
            if (!block.isUnMerged()) {
                block.setValue(block.getValue() * 2);
                score += block.getValue();
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

    private void drawBlocks() {
        for (Block block : blocks) {
            noStroke();
            fill(block.getColor().getRed(), block.getColor().getGreen(), block.getColor().getBlue());
            rect(block.getPosition().x + imageXOffset, block.getPosition().y + imageYOffset, tileSize, tileSize);
            noFill();
            drawNumber(block);
        }
    }

    private void drawNumber(Block block) {
        fill(0);
        textSize(20);
        textAlign(CENTER, CENTER);
        text(block.getValue(), block.getPosition().x + imageXOffset + tileSize / 2, block.getPosition().y + imageYOffset + tileSize / 2);
        noFill();
    }

    private void drawScore() {
        fill(0);
        textSize(30);
        textAlign(CENTER, CENTER);
        text(score, scoreXCord, scoreYCord);
        noFill();
    }

    private void spawnNewBlock() {
        Random random = new Random();
        do {
            boolean freeCordinates = true;
            int randomX = random.nextInt(tiles);
            int randomY = random.nextInt(tiles);
            int xCord = randomX * tileOffset;
            int yCord = randomY * tileOffset;
            for (Block block : blocks) {
                if (block.getPosition().x == xCord && block.getPosition().y == yCord) {
                    freeCordinates = false;
                }
            }

            if (freeCordinates) {
                Block block = new Block(new PVector(xCord, yCord));
                blocks.add(block);
                gameField[randomY][randomX] = block;
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
                        currentBlock.setNextPosition(new PVector(xRow * tileOffset, yPos * tileOffset));
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
                            currentBlock.setNextPosition(new PVector(xRow * tileOffset, yPos * tileOffset));
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
                        currentBlock.setNextPosition(new PVector(xPos * tileOffset, yRow * tileOffset));
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
                            currentBlock.setNextPosition(new PVector(xPos * tileOffset, yRow * tileOffset));
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
                        currentBlock.setNextPosition(new PVector(xRow * tileOffset, yPos * tileOffset));
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
                            currentBlock.setNextPosition(new PVector(xRow * tileOffset, yPos * tileOffset));
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
                        currentBlock.setNextPosition(new PVector(xPos * tileOffset, yRow * tileOffset));
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
                            currentBlock.setNextPosition(new PVector(xPos * tileOffset, yRow * tileOffset));
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