package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public class LevelBackgroundTransitionEventNode extends LevelEventNode
{
    public float r, g, b, a;
    public int transitDuration;

    public LevelBackgroundTransitionEventNode(float position, float r, float g, float b, float a, int transitDuration)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_BACKGROUND_TRANSITION, position);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.transitDuration = transitDuration;
    }
}
