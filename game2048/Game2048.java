package com.codegym.games.game2048;

import com.codegym.engine.cell.*;

import java.awt.font.FontRenderContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private boolean isGameStopped;
    private int score = 0;

    private int[][] gameField = new int[SIDE][SIDE];

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        isGameStopped = false;
        createGame();


        drawScene();
    }

    private void createGame() {
        gameField = new int[SIDE][SIDE];
        createNewNumber();
        createNewNumber();
    }

    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }

    private void createNewNumber() {
        if (getMaxTileValue() == 2048){
            win();
        }
        int x;
        int y;
        while (true) {
            x = getRandomNumber(SIDE);
            y = getRandomNumber(SIDE);
            int temp = getRandomNumber(10);
            if (gameField[y][x] == 0) {
                if (temp == 9) {
                    gameField[y][x] = 4;
                } else {
                    gameField[y][x] = 2;
                }
                break;
            }
        }
    }

    @Override
    public void onKeyPress(Key key) {
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                score = 0;
                setScore(score);
                createGame();
                drawScene();
            } else {
                return;
            }
        }

        if (!canUserMove()) {
            gameOver();
            return;
        }

        if (key == Key.UP) {
            moveUp();
        } else if (key == Key.RIGHT) {
            moveRight();
        } else if (key == Key.DOWN) {
            moveDown();
        } else if (key == Key.LEFT) {
            moveLeft();
        } else {
            return;
        }
        drawScene();
    }

    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }

    private void moveLeft() {
        boolean firstMove = false;
        for (int[] row : gameField) {
            boolean wasCompressed = compressRow(row);
            boolean wasMerged = mergeRow(row);
            if (wasMerged) {
                compressRow(row);
            }
            if (wasCompressed || wasMerged) {
                firstMove = true;
            }
        }
        if (firstMove) {
            createNewNumber();
        }
    }

    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }

    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise() {
        for (int i = 0; i < SIDE / 2; i++) {
            for (int j = i; j < SIDE - i - 1; j++) {
                int temp = gameField[i][j];
                gameField[i][j] = gameField[SIDE - 1 - j][i];
                gameField[SIDE - 1 - j][i] = gameField[SIDE - 1 - i][SIDE - 1 - j];
                gameField[SIDE - 1 - i][SIDE - 1 - j] = gameField[j][SIDE - 1 - i];
                gameField[j][SIDE - 1 - i] = temp;
            }
        }
    }

    private Color getColorByValue(int value) {
        switch (value) {
            case 0:
                return Color.ANTIQUEWHITE;
            case 2:
                return Color.ALICEBLUE;
            case 4:
                return Color.CORNFLOWERBLUE;
            case 8:
                return Color.MEDIUMPURPLE;
            case 16:
                return Color.CORAL;
            case 32:
                return Color.ORANGERED;
            case 64:
                return Color.ORANGE;
            case 128:
                return Color.DARKORANGE;
            case 265:
                return Color.INDIANRED;
            case 512:
                return Color.MISTYROSE;
            case 1024:
                return Color.RED;
            case 2048:
                return Color.DARKRED;
            default:
                return Color.NONE;
        }
    }

    private void setCellColoredNumber(int x, int y, int value) {
        Color color = getColorByValue(value);
        String str = value > 0 ? "" + value : "";
        setCellValueEx(x, y, color, str);
    }

    private boolean compressRow(int[] row) {
        int index = 0;
        boolean flag = false;
        for (int i = 0; i < SIDE; i++) {
            if (row[i] > 0) {
                if (i != index) {
                    row[index] = row[i];
                    row[i] = 0;
                    flag = true;
                }
                index++;
            }
        }
        return flag;
    }

    private boolean mergeRow(int[] row) {
        boolean result = false;
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] != 0 && row[i] == row[i + 1]) {
                row[i] += row[i + 1];
                row[i + 1] = 0;
                result = true;
                score += row[i];
                setScore(score);
            }
        }
        return result;
    }

    private int getMaxTileValue() {
        int temp = 0;
        for (int[] row : gameField){
            for (int x : row){
                if (x > temp){
                    temp = x;
                }
            }
        }
        return temp;

    }

    private void win(){
            isGameStopped = true;
            showMessageDialog(Color.GREEN,"You won!", Color.BLACK,300);
    }

    private boolean canUserMove() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x] == 0) {
                    return true;
                } else if (y < SIDE - 1 && gameField[y][x] == gameField[y + 1][x]) {
                    return true;
                } else if ((x < SIDE - 1) && gameField[y][x] == gameField[y][x + 1]) {
                    return true;
                }
            }
        }
        return false;
    }
    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.RED,"You lost!", Color.BLACK,300);
    }
}


