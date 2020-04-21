package com.andriusdgt.thedotsbackend.model;

import com.andriusdgt.thedotsbackend.annotation.Range;

import javax.validation.constraints.NotNull;

public class PointCoordinates {

    private String id;

    @NotNull(message = "{com.andriusdgt.thedotsbackend.model.PointCoordinates.listId.NotNull.message}")
    private String listId;

    @Range(min = -5000, max = 5000)
    private short x;

    @Range(min = -5000, max = 5000)
    private short y;

    public PointCoordinates() {
    }

    public PointCoordinates(String id, String listId, short x, short y) {
        this.id = id;
        this.listId = listId;
        this.x = x;
        this.y = y;
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
