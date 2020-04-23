package com.andriusdgt.thedotsbackend.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class PointList implements Serializable {

    @NotNull(message = "{com.andriusdgt.thedotsbackend.model.PointCoordinates.listId.NotNull.message}")
    private String id;

    @NotNull(message = "{com.andriusdgt.thedotsbackend.model.PointList.name.NotNull.message}")
    private String name;

    public PointList() {
    }

    public PointList(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
