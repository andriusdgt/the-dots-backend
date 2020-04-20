package com.andriusdgt.thedotsbackend.model;

public class PointCoordinates {

    private final short x;
    private final short y;

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
