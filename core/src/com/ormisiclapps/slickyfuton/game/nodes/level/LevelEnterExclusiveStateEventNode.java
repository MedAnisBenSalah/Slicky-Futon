package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by Anis on 9/17/2017.
 */

public class LevelEnterExclusiveStateEventNode extends LevelEventNode
{
    public int objects;
    public float speed;

    public LevelEnterExclusiveStateEventNode(float position, int objects, float speed)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_ENTER_EXCLUSIVE_STATE, position);
        this.objects = objects;
        this.speed = speed;
    }
}
