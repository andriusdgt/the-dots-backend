package com.andriusdgt.thedotsbackend.model;

import com.andriusdgt.thedotsbackend.annotation.Range;

public class PointCoordinates {

    @Range(min = -5000, max = 5000)
    private short x;

    @Range(min = -5000, max = 5000)
    private short y;

    public PointCoordinates(){}

    public PointCoordinates(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

}
