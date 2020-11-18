package com.codegym.games.snake;

import com.codegym.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    public int x;
    public int y;
    public boolean isAlive = true;
    private Direction direction = Direction.LEFT;

    private static final String HEAD_SIGN = "\uD83D\uDC7E";
    private static final String BODY_SIGN = "\u26AB";


    private List<GameObject> snakeParts;

    public Snake(int x, int y) {
        this.x = x;
        this.y = y;
        this.snakeParts = new ArrayList<>();
        snakeParts.add(new GameObject(x, y));
        snakeParts.add(new GameObject(x + 1, y));
        snakeParts.add(new GameObject(x + 2, y));

    }

    public void draw(Game game) {
        Color color = isAlive ? Color.BLACK : Color.RED;

        for (int i = 0; i < snakeParts.size(); i++) {
            GameObject part = snakeParts.get(i);
            String sign = (i != 0) ? BODY_SIGN : HEAD_SIGN;
            game.setCellValueEx(part.x, part.y, Color.NONE, sign, color, 75);
        }
    }


    public void move(Apple apple) {
        GameObject newHead = createNewHead();
        if (newHead.x >= SnakeGame.WIDTH
                || newHead.x < 0
                || newHead.y >= SnakeGame.HEIGHT
                || newHead.y < 0) {
            isAlive = false;
            return;
        }
        if (checkCollision(newHead)){
            isAlive = false;
            return;
        }
        snakeParts.add(0, newHead);
        if (newHead.x == apple.x && newHead.y == apple.y) {
            apple.isAlive = false;
        } else {
            removeTail();
        }

    }


    public GameObject createNewHead() {
        switch (direction) {
            case UP:
                return new GameObject(x, y - 1);
            case DOWN:
                return new GameObject(x, y + 1);
            case LEFT:
                return new GameObject(x - 1, y);
            case RIGHT:
                return new GameObject(x + 1, y);
        }
        return null;
    }

    public void removeTail() {
        snakeParts.remove(snakeParts.size() - 1);
    }

    public boolean checkCollision(GameObject gameObject) {
        for (int i = 0; i < snakeParts.size(); i++) {
            if (gameObject.x == snakeParts.get(i).x && gameObject.y == snakeParts.get(i).y) {
                return true;
            }
        }
        return false;
    }

    public void setDirection(Direction direction) {
        if ((this.direction == Direction.LEFT || this.direction == Direction.RIGHT) && snakeParts.get(0).x == snakeParts.get(1).x) {
            return;
        }
        if ((this.direction == Direction.UP || this.direction == Direction.DOWN) && snakeParts.get(0).y == snakeParts.get(1).y) {
            return;
        }

        if ((direction == Direction.UP && this.direction == Direction.DOWN)
                || (direction == Direction.LEFT && this.direction == Direction.RIGHT)
                || (direction == Direction.RIGHT && this.direction == Direction.LEFT)
                || (direction == Direction.DOWN && this.direction == Direction.UP))
            return;

        this.direction = direction;
    }
    
    public int getLength() {
        return snakeParts.size();
    }

}
