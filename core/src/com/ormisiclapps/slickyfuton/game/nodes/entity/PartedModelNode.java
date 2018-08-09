package com.ormisiclapps.slickyfuton.game.nodes.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Anis on 1/29/2017.
 */

public class PartedModelNode extends ModelNode {
    public Vector2[] positions;
    public float[] rotations;

    public PartedModelNode(int parts)
    {
        // Call the model node constructor
        super();
        // Create arrays
        positions = new Vector2[parts];
        rotations = new float[parts];
    }

    public PartedModelNode()
    {
        // Call the model node constructor
        super();
    }

    public void setPartsCount(int parts)
    {
        // Create arrays
        positions = new Vector2[parts];
        rotations = new float[parts];
    }

    public void setPart(int id, Vector2 position, float rotation)
    {
        positions[id] = new Vector2(position);
        rotations[id] = rotation;
    }
}
