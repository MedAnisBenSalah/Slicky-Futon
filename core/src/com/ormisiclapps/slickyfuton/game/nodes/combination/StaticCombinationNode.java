package com.ormisiclapps.slickyfuton.game.nodes.combination;

/**
 * Created by Anis on 12/14/2016.
 */

public class StaticCombinationNode extends CombinationNode
{
    public String[] position;

    public StaticCombinationNode(float speed, float endDistance, int count)
    {
        // Create the combination node
        super(speed, endDistance, count);
        // Create arrays
        position = new String[count];
    }

    public void addObject(int id, String model, String position, float distance, boolean passable)
    {
        // Set the combination node parameters
        super.addObject(id, model, distance, passable);
        // Save the position
        this.position[id] = position;
    }
}
