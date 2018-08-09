package com.ormisiclapps.slickyfuton.game.nodes.combination;

/**
 * Created by Anis on 12/14/2016.
 */

public class ComposedCombinationNode extends CombinationNode
{
    public int[] movementId;
    public int[] startingPoint1;
    public int[] startingPoint2;

    public ComposedCombinationNode(float speed, float endDistance, int count)
    {
        // Create the combination node
        super(speed, endDistance, count);
        // Create arrays
        startingPoint1 = new int[count];
        startingPoint2 = new int[count];
        movementId = new int[count];
    }

    public void addObject(int id, String model, int movementId, int startingPoint1, int startingPoint2, float distance, boolean passable)
    {
        // Set the combination node parameters
        super.addObject(id, model, distance, passable);
        // Save parameters
        this.movementId[id] = movementId;
        this.startingPoint1[id] = startingPoint1;
        this.startingPoint2[id] = startingPoint2;
    }
}
