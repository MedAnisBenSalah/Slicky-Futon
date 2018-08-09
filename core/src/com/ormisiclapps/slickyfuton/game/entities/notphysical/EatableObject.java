package com.ormisiclapps.slickyfuton.game.entities.notphysical;

import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;

/**
 * Created by Anis on 2/28/2017.
 */

public class EatableObject extends GameObject
{

    public EatableObject(String name)
    {
        // Create the entity
        super(name, false);
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        super.create(position, movementId, startingPoint, speed);
        // Set the user's data
        getBody().setUserData("Eatable");
    }
}
