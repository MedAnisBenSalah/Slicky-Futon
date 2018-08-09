package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by Anis on 9/16/2017.
 */

public class LevelEntitiesColorTransitionEventNode extends LevelEventNode
{
    public float r, g, b, a;
    public int transitDuration;

    public LevelEntitiesColorTransitionEventNode(float position, float r, float g, float b, float a, int transitDuration)
    {
        super(LevelEventNode.LevelEventType.LEVEL_EVENT_TYPE_ENTITIES_COLOR_TRANSITION, position);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.transitDuration = transitDuration;
    }
}
