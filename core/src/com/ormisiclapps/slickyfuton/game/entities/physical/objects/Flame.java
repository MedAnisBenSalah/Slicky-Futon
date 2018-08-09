package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.animation.EntityAnimation;
import com.ormisiclapps.slickyfuton.game.core.GameLogic;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.game.world.Terrain;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by Anis on 11/25/2016.
 */

public class Flame extends GameObject
{
    private EntityAnimation animation;
    private Vector2 animationScreenPosition;
    private Vector2 animationScreenSize;

    private static final float VELOCITY = 120;
    private static final float ANIMATION_FRAME_DURATION = 0.05f;

    public Flame()
    {
        // Create the object
        super("Flame", true);
        // Get the flame animation texture
        TextureRegion texture = Core.getInstance().getResourcesManager().getResource("FlameAnimation", ResourceType.RESOURCE_TYPE_MODEL);
        // Create the animation
        animation = new EntityAnimation(texture, 2, 3, ANIMATION_FRAME_DURATION, true);
        // Create vectors
        animationScreenPosition = new Vector2();
        animationScreenSize = new Vector2();
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the object
        super.create(position, movementId, startingPoint, GameLogic.getInstance().isEndless() ? VELOCITY :
                Terrain.getInstance().getLevelFlameSpeed());

        // Set the flame's velocity
        setVelocity(new Vector2(GameLogic.getInstance().isEndless() ? VELOCITY : Terrain.getInstance().getLevelFlameSpeed(), 0));
        // Start the animation
        animation.start();
        // Set the animation screen size
        animationScreenSize.set(GameMath.pixelsPerMeters(getSize()));
    }

    @Override
    public void process()
    {
        super.process();
        // Get the animations's screen position
        animationScreenPosition.set(Camera.getInstance().worldToScreen(getPosition()).sub(animationScreenSize.x / 2,
                animationScreenSize.y / 2));
    }

    @Override
    public void render()
    {
        // Render the animation
        animation.render(animationScreenPosition, animationScreenSize, 270f, getColor());
    }
}
