package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public class LevelSpeedChangeEventNode extends LevelEventNode
{
    public float speed;

    public LevelSpeedChangeEventNode(float position, float speed)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_SPEED_CHANGE, position);
        this.speed = speed;
    }
}
