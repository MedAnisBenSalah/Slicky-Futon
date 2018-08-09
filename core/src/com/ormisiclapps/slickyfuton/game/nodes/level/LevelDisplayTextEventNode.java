package com.ormisiclapps.slickyfuton.game.nodes.level;

/**
 * Created by OrMisicL on 9/9/2017.
 */

public class LevelDisplayTextEventNode extends LevelEventNode
{
    public String text;
    public float screenPositionX, screenPositionY;
    public float endPosition;

    public LevelDisplayTextEventNode(float position, String text, float screenPositionX, float screenPositionY, float endPosition)
    {
        super(LevelEventType.LEVEL_EVENT_TYPE_DISPLAY_TEXT, position);
        this.text = text;
        this.screenPositionX = screenPositionX;
        this.screenPositionY = screenPositionY;
        this.endPosition = endPosition;
    }
}
