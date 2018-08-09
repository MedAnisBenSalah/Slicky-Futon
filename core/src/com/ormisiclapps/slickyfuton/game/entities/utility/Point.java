package com.ormisiclapps.slickyfuton.game.entities.utility;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by Anis on 10/3/2016.
 */

public class Point
{
    private Vector2 position;
    private Vector2 screenUpperPosition;
    private Vector2 screenLowerPosition;
    private Vector2 tmpVector;

    public Point()
    {
        // Create vectors
        position = new Vector2();
        screenUpperPosition = new Vector2();
        screenLowerPosition = new Vector2();
        tmpVector = new Vector2();
    }

    public void process(int position, int maxPoints, Vector2 previousPosition)
    {
        // Skip first point
        if(previousPosition == null)
            screenUpperPosition.set(screenLowerPosition.set(Camera.getInstance().worldToScreen(this.position)));
        else
        {
            // Calculate the size
            float factor = (float)position / (float)maxPoints;
            float size = Configuration.POINT_SIZE * factor;
            // Calculate the distance
            tmpVector.set(this.position).sub(previousPosition).nor();
            // Calculate the perpendicular
            tmpVector.set(-tmpVector.y, tmpVector.x).scl(size);
            // Calculate the upper position
            screenUpperPosition.set(this.position.x + tmpVector.x, this.position.y + tmpVector.y);
            // Calculate the lower position
            screenLowerPosition.set(this.position.x - tmpVector.x, this.position.y - tmpVector.y);
            // Convert to screen position
            screenUpperPosition.set(Camera.getInstance().worldToScreen(screenUpperPosition));
            screenLowerPosition.set(Camera.getInstance().worldToScreen(screenLowerPosition));
        }
    }

    public void setPosition(Vector2 position)
    {
        this.position.set(position);
    }

    public Vector2 getScreenUpperPosition() {
        return screenUpperPosition;
    }

    public Vector2 getScreenLowerPosition() {
        return screenLowerPosition;
    }

    public Vector2 getPosition() {
        return position;
    }
}
