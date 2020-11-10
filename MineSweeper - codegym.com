//https://codegym.cc/projects/apps/37209?fbclid=IwAR0wjDAK1Zz0I3vP7jL5AhSiP1kTNAFek0SUDTEYJQEbaayClyXJ-3M7t4E

package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;


public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE*SIDE;
    private int score;


    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
//        isGameStopped = false;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.BEIGE);
            }

        }
        countFlags = countMinesOnField;
        countMineNeighbors();
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x, y, "");
                setCellColor(x, y, Color.BEIGE);
            }
        }
    }

    private void gameOver(){
        isGameStopped=true;
        showMessageDialog(Color.BEIGE, "BOOM!",Color.BLACK, 50);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.BEIGE, "WIN!",Color.BLACK, 50);
    }

    private void restart(){
        isGameStopped=false;
        countClosedTiles = SIDE*SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        createGame();

    }


    private void openTile(int x, int y) {
        if (isGameStopped){
            return;
        }
        GameObject gameObject = gameField[y][x];
        if (gameObject.isFlag){
            return;
        }
        if (!gameObject.isOpen) {
            if (gameObject.isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameObject.isOpen = true;
                gameOver();
            } else {
                countClosedTiles--;
                score+=5;
                setScore(score);
                if (countClosedTiles==countMinesOnField){
                    win();
                }
                if (gameObject.countMineNeighbors == 0) {
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.ALICEBLUE);
                    gameObject.isOpen = true;
                    List<GameObject> list = getNeighbors(gameObject);
                    for (GameObject go : list) {
                        openTile(go.x, go.y);
                    }
                } else {
                    setCellNumber(x, y, gameObject.countMineNeighbors);
                    setCellColor(x, y, Color.ALICEBLUE);
                    gameObject.isOpen = true;
                }

            }
            gameObject.isOpen = true;
        }
        return;
    }

    private void markTile(int x, int y) {
        if (isGameStopped){
            return;
        }
        GameObject gameObject = gameField[y][x];
        if (gameObject.isOpen){
            return;
        }
        if (gameObject.isFlag){
            gameObject.isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.BEIGE);
        } else {
            if (countFlags ==0){
                return;
            }
            gameObject.isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.BLANCHEDALMOND);
        }

    }


    @Override
    public void onMouseLeftClick(int x, int y) {
        openTile(x, y);
        if (isGameStopped){
            restart();
        } else {
            return;
        }
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if (gameField[y][x].isMine) {
                    continue;
                } else {
                    List<GameObject> temp = getNeighbors(gameField[y][x]);
                    for (GameObject gm : temp) {
                        if (gm.isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}
