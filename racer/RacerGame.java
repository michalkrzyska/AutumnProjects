package com.codegym.games.racer;

import com.codegym.engine.cell.*;
import com.codegym.games.racer.road.RoadManager;


public class RacerGame extends Game {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    public static final int CENTER_X = WIDTH/2;
    public static final int ROADSIDE_WIDTH = 14;
    private static final int RACE_GOAL_CARS_COUNT = 40;

    private RoadMarking roadMarking;
    private PlayerCar player;
    private RoadManager roadManager;
    private FinishLine finishLine;
    private ProgressBar progressBar;
    private int score;

    private boolean isGameStopped;

    @Override
    public void initialize() {
        showGrid(false);
        setScreenSize(WIDTH,HEIGHT);
        createGame();
    }

    private void createGame(){
        roadMarking = new RoadMarking();
        player = new PlayerCar();
        roadManager = new RoadManager();
        finishLine = new FinishLine();
        progressBar = new ProgressBar(RACE_GOAL_CARS_COUNT);
        drawScene();
        setTurnTimer(40);
        isGameStopped = false;
        score= 3500;
        

    }

    private void drawScene() {
        drawField();
        roadMarking.draw(this);
        player.draw(this);
        roadManager.draw(this);
        finishLine.draw(this);
        progressBar.draw(this);
    }

    private void drawField() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (j >= 14 && j < (WIDTH - ROADSIDE_WIDTH)) {
                    setCellColor(j, i, Color.DARKGREY);
                } else {
                    setCellColor(j, i, Color.GREEN);
                }
            }
            setCellColor(CENTER_X, i, Color.WHITE);
        }
    }

    @Override
    public void setCellColor(int x, int y, Color color) {
        if (x > WIDTH - 1 || x < 0 || y < 0 || y > HEIGHT - 1){
            return;
        }
        super.setCellColor(x, y, color);
    }

    @Override
    public void onTurn(int step) {
        if (roadManager.checkCrash(player)){
            gameOver();
            drawScene();
            return;
        }
        if (roadManager.getPassedCarsCount() >= RACE_GOAL_CARS_COUNT){
            finishLine.show();
        }
        if (finishLine.isCrossed(player)){
            win();
            drawScene();
            return;
        }
        moveAll();
        roadManager.generateNewRoadObjects(this);
        score-=5;
        setScore(score);
        drawScene();
        
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.RIGHT){
            player.setDirection(Direction.RIGHT);
        } else if (key == Key.LEFT){
            player.setDirection(Direction.LEFT);
        } else if (key == Key.SPACE){
            if (isGameStopped){
                createGame();
            }
        } else if (key == Key.UP){
            player.speed = 2;
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if ((key == Key.RIGHT && player.getDirection()==Direction.RIGHT )|| (key == Key.LEFT && player.getDirection()==Direction.LEFT)){
            player.setDirection(Direction.NONE);
        }else if (key == Key.UP){
            player.speed = 1;
        }
    }

    private void moveAll() {
        roadMarking.move(player.speed);
        player.move();
        roadManager.move(player.speed);
        finishLine.move(player.speed);
        progressBar.move(roadManager.getPassedCarsCount());

    }


    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "GAME OVER!", Color.RED, 50);
        stopTurnTimer();
        player.stop();

    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Win! Your score: " + score, Color.GREEN, 75);
        stopTurnTimer();
    }

}
