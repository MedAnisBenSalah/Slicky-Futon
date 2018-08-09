package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.utility.Configuration;

/**
 * Created by Anis on 12/25/2016.
 */

public class ChainsawStick extends GameObject
{
    private static final float INITIAL_ROTATION = 270f;

    public ChainsawStick(boolean passable)
    {
        // Create the game object
        super("ChainsawStick", passable);
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Fix the position
        if(position.y >= Configuration.TERRAIN_ROOF_POSITION / 2)
            position.set(position.x, Configuration.TERRAIN_ROOF_POSITION - 6.125f);
        else
            position.set(position.x, 6.125f);

        // Create the game object
        super.create(position, movementId, startingPoint, speed);
        // Find the stick's position
        if(position.y >= Configuration.TERRAIN_ROOF_POSITION / 2)
            setRotationInDegrees(INITIAL_ROTATION - 180f);
        else
            setRotationInDegrees(INITIAL_ROTATION);
    }
}
