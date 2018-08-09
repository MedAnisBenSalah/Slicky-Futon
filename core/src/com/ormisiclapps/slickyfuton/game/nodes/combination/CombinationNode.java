package com.ormisiclapps.slickyfuton.game.nodes.combination;

/**
 * Created by Anis on 11/27/2016.
 */

public abstract class CombinationNode
{
    public float speed;
    public float endDistance;
    public String[] model;
    public float[] distance;
    public boolean[] passable;

    protected CombinationNode(float speed, float endDistance, int count)
    {
        // Set parameters
        this.speed = speed;
        this.endDistance = endDistance;
        // Create arrays
        model = new String[count];
        distance = new float[count];
        passable = new boolean[count];
    }

    protected void addObject(int id, String model, float distance, boolean passable)
    {
        this.model[id] = model;
        this.distance[id] = distance;
        this.passable[id] = passable;
    }
}
