package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/10/2017.
 */

public class LevelCompletedEvent extends LevelEventNode
{
    public LevelCompletedEvent(float position)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_LEVEL_COMPLETED, position);
    }
}
