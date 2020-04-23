package com.andriusdgt.thedotsbackend.model;

import com.andriusdgt.thedotsbackend.annotation.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PointCoordinates implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointCoordinates)) return false;

        PointCoordinates that = (PointCoordinates) o;

        if (getX() != that.getX()) return false;
        if (getY() != that.getY()) return false;
        return getListId().equals(that.getListId());
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + (int) getY();
        result = 31 * result + getListId().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getX() + " " + getY();
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
