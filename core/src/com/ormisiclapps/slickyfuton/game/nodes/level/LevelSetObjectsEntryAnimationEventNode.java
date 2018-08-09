package com.ormisiclapps.slickyfuton.game.nodes.level;

import com.ormisiclapps.slickyfuton.enumerations.ObjectsEntryType;

/**
 * Created by OrMisicL on 9/29/2017.
 */

public class LevelSetObjectsEntryAnimationEventNode extends LevelEventNode
{
    public ObjectsEntryType type;
    public float speed;

    public LevelSetObjectsEntryAnimationEventNode(float position, ObjectsEntryType type, float speed)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_SET_OBJECTS_ENTRY_ANIMATION, position);
        // Set parameters
        this.type = type;
        this.speed = speed;
    }
}
