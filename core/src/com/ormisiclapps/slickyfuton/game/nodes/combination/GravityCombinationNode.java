package com.ormisiclapps.slickyfuton.game.nodes.combination;

/**
 * Created by OrMisicL on 6/27/2017.
 */

public class GravityCombinationNode extends CombinationNode
{
    public String[] position;
    public int[] movement;
    public int[] startingPoint;

    public GravityCombinationNode(float speed, float endDistance, int count)
    {
        // Create the default combination node
        super(speed, endDistance, count);
        // Create arrays
        position = new String[count];
        movement = new int[count];
        startingPoint = new int[count];
    }

    public void addObject(int id, String model, String position, int movement, int startingPoint, float distance, boolean passable)
    {
        super.addObject(id, model, distance, passable);
        // Set the position
        this.position[id] = position;
        // Set the movement
        this.movement[id] = movement;
        // Set the starting point
        this.startingPoint[id] = startingPoint;
    }
}
