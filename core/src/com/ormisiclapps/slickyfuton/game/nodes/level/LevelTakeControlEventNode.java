package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public class LevelTakeControlEventNode extends LevelEventNode
{
    public float positionY;
    public float speed;
    public float shuffleDistance;

    public LevelTakeControlEventNode(float position, float positionY, float speed, float shuffleDistance)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_TAKE_CONTROL, position);
        this.positionY = positionY;
        this.speed = speed;
        this.shuffleDistance = shuffleDistance;
    }
}
