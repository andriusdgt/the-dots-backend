package com.andriusdgt.thedotsbackend.model;

import com.andriusdgt.thedotsbackend.annotation.Range;

import javax.validation.constraints.NotNull;

public class PointCoordinates {

    private String id;

    @Range(min = -5000, max = 5000)
    private short x;

    @Range(min = -5000, max = 5000)
    private short y;

    @NotNull(message = "{com.andriusdgt.thedotsbackend.model.PointCoordinates.listId.NotNull.message}")
    private String listId;

    public PointCoordinates() {
    }

    public PointCoordinates(short x, short y, String listId) {
        this.x = x;
        this.y = y;
        this.listId = listId;
    }

    public String getId() {
        return id;
    }

    public String getListId() {
        return listId;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

}
