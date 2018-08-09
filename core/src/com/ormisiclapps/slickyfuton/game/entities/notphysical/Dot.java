package com.ormisiclapps.slickyfuton.game.entities.notphysical;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.animation.EntityAnimation;
import com.ormisiclapps.slickyfuton.game.entities.physical.Player;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by OrMisicL on 7/6/2017.
 */

public class Dot extends TerrainObject
{
    private EntityAnimation animation;

    public Dot(String model, Vector2 position)
    {
        // Create the terrain object
        super(model, position);
        // Create the animation instance
        TextureRegion texture = Core.getInstance().getResourcesManager().getResource("Terrain/Dot",
                ResourceType.RESOURCE_TYPE_TEXTURE_REGION);

        animation = new EntityAnimation(texture, 3, 3, 0.05f, true);
        // Start the animation
        animation.start();
    }

    @Override
    public void render()
    {
        // Render animation
        animation.render(screenPosition, screenSize, 0f, Color.WHITE);
    }
}
