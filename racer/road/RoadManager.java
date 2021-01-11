package com.codegym.games.racer.road;

import com.codegym.engine.cell.Game;
import com.codegym.games.racer.GameObject;
import com.codegym.games.racer.PlayerCar;
import com.codegym.games.racer.RacerGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - RacerGame.ROADSIDE_WIDTH;

    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;

    private List<RoadObject> items = new ArrayList<>();

    private int passedCarsCount = 0;

    public int getPassedCarsCount() {
        return passedCarsCount;
    }

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.SPIKE) {
            return new Spike(x, y);
        } else if (type == RoadObjectType.DRUNK_CAR) {
            return new MovingCar(x, y);
        } else {
            return new Car(type, x, y);
        }
    }

    private void addRoadObject(RoadObjectType roadObjectType, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(roadObjectType);
        RoadObject roadObject = createRoadObject(roadObjectType, x, y);

        if (isRoadSpaceFree(roadObject)) {
            items.add(roadObject);
        }

    }

    public void draw(Game game) {
        for (RoadObject item : items) {
            item.draw(game);
        }
    }

    public void move(int boost) {
        for (RoadObject item : items) {
            item.move(boost + item.speed, items);
        }
        deletePassedItems();
    }

    private boolean movingCarExists() {
        for (RoadObject r : items){
            if (r.type == RoadObjectType.DRUNK_CAR){
                return true;
            }
        }
        return false;
    }

    private void generateMovingCar(Game game){
        if (game.getRandomNumber(100) < 10 && !movingCarExists()) {
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
        }
    }

    private boolean spikeExists() {
        for (RoadObject r : items) {
            if (r.type == RoadObjectType.SPIKE) {
                return true;
            }
        }
        return false;
    }

    private void generateSpike(Game game) {
        if (game.getRandomNumber(100) < 10 && !spikeExists()) {
            addRoadObject(RoadObjectType.SPIKE, game);
        }
    }

    private void generateRegularCar(Game game) {
        if (game.getRandomNumber(100) < 30) {
            int carTypeNumber = game.getRandomNumber(4);
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
        }
    }

    public void generateNewRoadObjects(Game game) {
        generateSpike(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        Iterator<RoadObject> iterator = items.iterator();
        while (iterator.hasNext()) {
            RoadObject  r = iterator.next();
            if (r.y >= RacerGame.HEIGHT) {
                if (r.type != RoadObjectType.SPIKE){
                    passedCarsCount++;
                }
                iterator.remove();
            }
        }
    }

    public boolean checkCrash(PlayerCar playerCar) {
        for (RoadObject r : items) {
            if (r.isCollision(playerCar)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for (RoadObject r : items) {
            if (r.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE)) {
                return false;
            }
        }
        return true;
    }
}