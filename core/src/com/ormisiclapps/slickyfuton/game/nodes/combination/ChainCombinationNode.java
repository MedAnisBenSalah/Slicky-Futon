package com.ormisiclapps.slickyfuton.game.nodes.combination;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Anis on 1/14/2017.
 */

public class ChainCombinationNode extends CombinationNode
{
    public class ChainObjectPoints
    {
        public Vector2[] groundPositions;
        public Vector2[] roofPositions;

        private ChainObjectPoints(int points)
        {
            // Create the positions arrays
            groundPositions = new Vector2[points];
            roofPositions = new Vector2[points];
        }

        private void addPoint(int id, Vector2 groundPosition, Vector2 roofPosition)
        {
            this.groundPositions[id] = new Vector2(groundPosition);
            this.roofPositions[id] = new Vector2(roofPosition);
        }
    }

    public ChainObjectPoints[] objectPoints;
    public String[] flags;

    public ChainCombinationNode(float endDistance, int count)
    {
        // Create the combination node
        super(0f, endDistance, count);
        // Create arrays
        objectPoints = new ChainObjectPoints[count];
        flags = new String[count];
    }

    public void addObject(int id, String model, boolean passable, String flags, int points)
    {
        // Create the combination node
        super.addObject(id, model, 0f, passable);
        // Set the flags
        this.flags[id] = flags;
        // Create the objects array
        objectPoints[id] = new ChainObjectPoints(points);
    }

    public void addObjectPoint(int object, int point, Vector2 groundPosition, Vector2 roofPosition)
    {
        objectPoints[object].addPoint(point, groundPosition, roofPosition);
    }
}
