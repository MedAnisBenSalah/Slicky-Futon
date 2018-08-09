package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public abstract class LevelEventNode
{
    public enum LevelEventType
    {
        LEVEL_EVENT_TYPE_BACKGROUND_TRANSITION,
        LEVEL_EVENT_TYPE_SPEED_CHANGE,
        LEVEL_EVENT_TYPE_TAKE_CONTROL,
        LEVEL_EVENT_TYPE_RESTORE_CONTROL,
        LEVEL_EVENT_TYPE_DISPLAY_TEXT,
        LEVEL_EVENT_TYPE_TOGGLE_LETHAL_GROUND,
        LEVEL_EVENT_TYPE_ENTITIES_COLOR_TRANSITION,
        LEVEL_EVENT_TYPE_ENTER_EXCLUSIVE_STATE,
        LEVEL_EVENT_TYPE_SET_OBJECTS_ENTRY_ANIMATION,
        LEVEL_EVENT_TYPE_LEVEL_COMPLETED
    }

    public LevelEventType type;
    public float position;

    public LevelEventNode(LevelEventType type, float position)
    {
        this.type = type;
        this.position = position;
    }
}