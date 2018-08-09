package com.ormisiclapps.slickyfuton.game.nodes.movement;

/**
 * Created by Anis on 10/30/2016.
 */

public class MovementNode {
    public String type;

    public int points;
    public float distance;

    public String[] pointsX;
    public String[] pointsY;

    public float rotationSpeed;

    public MovementNode()
    {

    }

    public void setNode(String type, int points, float distance, float rotationSpeed)
    {
        this.type = type;
        this.points = points;
        this.distance = distance;
        this.rotationSpeed = rotationSpeed;
        // Create the points position arrays
        pointsX = new String[points];
        pointsY = new String[points];
    }

    public void setPoint(int position, String pointX, String pointY)
    {
        pointsX[position] = pointX;
        pointsY[position] = pointY;
    }


}
