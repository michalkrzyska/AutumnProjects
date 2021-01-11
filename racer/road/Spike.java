package com.codegym.games.racer.road;

import com.codegym.games.racer.GameObject;
import com.codegym.games.racer.ShapeMatrix;

public class Spike  extends RoadObject {

    public Spike(int x, int y) {
        super(RoadObjectType.SPIKE, x, y);
        speed = 0;
    }
}
