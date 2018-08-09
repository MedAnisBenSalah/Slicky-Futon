package com.ormisiclapps.slickyfuton.game.nodes.combination;

import com.ormisiclapps.slickyfuton.game.nodes.combination.CombinationNode;

/**
 * Created by Anis on 12/14/2016.
 */

public class SimpleCombinationNode extends CombinationNode
{
    public int[] movementId;
    public String[] position;
    public int[] startingPoint;

    public SimpleCombinationNode(float speed, float endDistance, int count)
    {
        // Create the combination node
        super(speed, endDistance, count);
        // Create arrays
        position = new String[count];
        startingPoint = new int[count];
        movementId = new int[count];
    }

    public void addObject(int id, String model, int movementId, String position, int startingPoint, float distance, boolean passable)
    {
        // Set the combination node parameters
        super.addObject(id, model, distance, passable);
        // Save parameters
        this.movementId[id] = movementId;
        this.position[id] = position;
        this.startingPoint[id] = startingPoint;
    }
}
