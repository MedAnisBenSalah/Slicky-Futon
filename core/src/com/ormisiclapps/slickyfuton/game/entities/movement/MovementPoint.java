package com.ormisiclapps.slickyfuton.game.entities.movement;

import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.enumerations.GameMode;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.enumerations.MovementPointType;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by Anis on 10/30/2016.
 */

public class MovementPoint
{
    private MovementPointType pointX;
    private MovementPointType pointY;
    private Vector2 position;

    public MovementPoint(Vector2 position, String pointX, String pointY)
    {
        // Find the movement types for each axis
        this.pointX = getMovementPointType(pointX);
        this.pointY = getMovementPointType(pointY);
        // Get the roof position
        float roofPosition = GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ? Configuration.TERRAIN_ROOF_POSITION :
                Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION;

        float path = GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ?  Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH :
                Configuration.GRAVITY_SWITCH_CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH;

        // Get the x axis position
        float x = position.x;
        if(this.pointX == MovementPointType.MOVEMENT_POINT_TYPE_LEFT)
            x -= Configuration.MOVABLE_OBJECT_PATH_LENGTH;
        else if(this.pointX == MovementPointType.MOVEMENT_POINT_TYPE_RIGHT)
            x += Configuration.MOVABLE_OBJECT_PATH_LENGTH;

        // Get the y axis position
        float y = position.y;
        if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_UP)
            y = roofPosition / 2 + path;
        else if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_DOWN)
            y = roofPosition / 2 - path;
        else if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_MIDDLE)
            y = roofPosition / 2;

        // Create the point vector
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition() { return position; }

    public MovementPointType getPointTypeY() { return pointY; }

    public void setPosition(Vector2 position)
    {
        // Get the roof position
        float roofPosition = GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ? Configuration.TERRAIN_ROOF_POSITION :
                Configuration.TERRAIN_GRAVITY_SWITCH_ROOF_POSITION;

        float path = GameLogic.getInstance().getGameMode() == GameMode.GAME_MODE_FLYING ?  Configuration.CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH :
                Configuration.GRAVITY_SWITCH_CIRCULAR_MOVABLE_OBJECT_PATH_LENGTH;

        // Get the x axis position
        float x = position.x;
        if(this.pointX == MovementPointType.MOVEMENT_POINT_TYPE_LEFT)
            x -= Configuration.MOVABLE_OBJECT_PATH_LENGTH;
        else if(this.pointX == MovementPointType.MOVEMENT_POINT_TYPE_RIGHT)
            x += Configuration.MOVABLE_OBJECT_PATH_LENGTH;

        // Get the y axis position
        float y = position.y;
        if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_UP)
            y = roofPosition / 2 + path;
        else if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_DOWN)
            y = roofPosition / 2 - path;
        else if(this.pointY == MovementPointType.MOVEMENT_POINT_TYPE_MIDDLE)
            y = roofPosition / 2;

        // Create the point vector
        this.position.set(x, y);
    }

    private static MovementPointType getMovementPointType(String movementType)
    {
        if(movementType.equals("Up"))
            return MovementPointType.MOVEMENT_POINT_TYPE_UP;
        else if(movementType.equals("Down"))
            return MovementPointType.MOVEMENT_POINT_TYPE_DOWN;
        else if(movementType.equals("Left"))
            return MovementPointType.MOVEMENT_POINT_TYPE_LEFT;
        else if(movementType.equals("Right"))
            return MovementPointType.MOVEMENT_POINT_TYPE_RIGHT;
        else if(movementType.equals("Middle"))
            return MovementPointType.MOVEMENT_POINT_TYPE_MIDDLE;
        else
            return MovementPointType.MOVEMENT_POINT_TYPE_NONE;
    }
}
