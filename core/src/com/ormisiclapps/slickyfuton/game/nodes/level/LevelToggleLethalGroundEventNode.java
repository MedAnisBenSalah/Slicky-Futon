package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by Anis on 9/16/2017.
 */

public class LevelToggleLethalGroundEventNode extends LevelEventNode
{
    public boolean toggle;

    public LevelToggleLethalGroundEventNode(float position, boolean toggle)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_TOGGLE_LETHAL_GROUND, position);
        this.toggle = toggle;
    }
}
