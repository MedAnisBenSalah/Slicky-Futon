package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public class LevelObjectNode
{
    public String model;
    public float positionX, positionY;
    public float sizeX, sizeY;
    public int movementId, startingPoint;
    public float speed;

    public LevelObjectNode(String model, float positionX, float positionY, float sizeX, float sizeY, int movementId, int startingPoint,
                           float speed)
    {
        this.model = model;
        this.positionX = positionX;
        this.positionY = positionY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.movementId = movementId;
        this.startingPoint = startingPoint;
        this.speed = speed;
    }
}
