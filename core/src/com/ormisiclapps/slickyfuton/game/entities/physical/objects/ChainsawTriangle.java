package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.utility.Configuration;

/**
 * Created by OrMisicL on 6/26/2017.
 */

public class ChainsawTriangle extends GameObject
{
    public ChainsawTriangle(boolean passable)
    {
        // Create the game object
        super("ChainsawTriangle", passable);
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the game object
        super.create(position, movementId, startingPoint, speed);
        // Find the triangle's flip
        if(movementId > 0)//GameIntelligence.getInstance().isChainsawTriangleFlipped())
            flip();
    }

    @Override
    public void process()
    {
        // Update the object's color
        if(!getColor().equals(GameIntelligence.getInstance().getEntitiesColor()))
            setColor(GameIntelligence.getInstance().getEntitiesColor());

        // Process object
        super.process();
    }
}

