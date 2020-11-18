package com.codegym.games.snake;

import com.codegym.engine.cell.*;

public class SnakeGame extends Game {

    public static final int WIDTH = 15;
    public static final int HEIGHT = 15;
    private boolean isGameStopped;
    private static final int GOAL = 28;

    private Snake snake = new Snake(WIDTH / 2, HEIGHT / 2);
    private Apple apple;
    private int turnDelay;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }


    private void createGame() {
        isGameStopped = false;
        score = 0;
        setScore(score);
        snake = new Snake(WIDTH / 2, HEIGHT / 2);
        turnDelay = 300;
        setTurnTimer(turnDelay);

        createNewApple();
        drawScene();

    }

    private void drawScene() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                setCellValueEx(i, j, Color.DARKSEAGREEN, "");
            }
        }
        snake.draw(this);
        apple.draw(this);
    }


    private void createNewApple() {
        while (true) {
            Apple newApple;
            int x = getRandomNumber(WIDTH);
            int y = getRandomNumber(HEIGHT);
            newApple = new Apple(x, y);
            if (snake.checkCollision(newApple)){
                continue;
            }else {
                apple = newApple;
                break;
            }
        }
    }
    
    @Override
    public void onKeyPress(Key key) {
        if (key == key.LEFT) {
            snake.setDirection(Direction.LEFT);

        } else if (key == key.RIGHT) {
            snake.setDirection(Direction.RIGHT);
        } else if (key == key.UP) {
            snake.setDirection(Direction.UP);
        } else if (key == key.DOWN) {
            snake.setDirection(Direction.DOWN);
        } else if ((key == key.SPACE) && (isGameStopped)){
            createGame();
        }
    }

    private void gameOver(){
        stopTurnTimer();
        isGameStopped = true;
        showMessageDialog(Color.RED,"You lost!", Color.BLACK,300);

    }

    private void win() {
            stopTurnTimer();
            isGameStopped = true;
            showMessageDialog(Color.GREEN,"You won!", Color.BLACK,300);
        }

    @Override
    public void onTurn(int step) {
        snake.move(apple);
        if (!apple.isAlive){
            createNewApple();
            score+=5;
            setScore(score);
            turnDelay-=10;
            setTurnTimer(turnDelay);
        }
        if (!snake.isAlive){
            gameOver();
        }
        if  (snake.getLength() > GOAL) {
            win();
        }

        drawScene();
    }
}
