package com.ormisiclapps.slickyfuton.game.nodes.combination;

/**
 * Created by Anis on 2/3/2017.
 */

public class PartedCombinationNode extends CombinationNode
{
    public int[] startingPoint;

    public PartedCombinationNode(float speed, float endDistance, int count)
    {
        super(speed, endDistance, count);
        // Create arrays
        startingPoint = new int[count];
    }

    public void addObject(int id, String model, int startingPoint, float distance, boolean passable)
    {
        // Add the object
        super.addObject(id, model, distance, passable);
        // Set the starting point
        this.startingPoint[id] = startingPoint;
    }
}
