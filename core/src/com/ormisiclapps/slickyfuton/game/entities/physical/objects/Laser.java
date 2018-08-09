package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.enumerations.ResourceType;
import com.ormisiclapps.slickyfuton.game.animation.EntityAnimation;
import com.ormisiclapps.slickyfuton.game.core.GameIntelligence;
import com.ormisiclapps.slickyfuton.game.entities.Camera;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;
import com.ormisiclapps.slickyfuton.game.world.GameWorld;
import com.ormisiclapps.slickyfuton.utility.Configuration;
import com.ormisiclapps.slickyfuton.utility.GameMath;

/**
 * Created by Anis on 11/23/2016.
 */

public class Laser extends GameObject
{
    private Vector2 laserPosition;
    private Vector2 laserSize;
    private Texture laserTexture;
    private boolean laserDown;
    private Body rayBody;
    private Vector2 tmpVector;
    private EntityAnimation animation;
    private Vector2 lighteningPosition;
    private Vector2 lighteningSize;
    private Color animationColor;

    private static final float RAY_WIDTH = 0.5f;

    public Laser()
    {
        super("Laser", true);
        // Get the laser texture
        laserTexture = Core.getInstance().getResourcesManager().getResource("Empty", ResourceType.RESOURCE_TYPE_TEXTURE);
        // Create vectors
        laserPosition = new Vector2();
        laserSize = new Vector2();
        lighteningPosition = new Vector2();
        lighteningSize = new Vector2();
        tmpVector = new Vector2();
        animationColor = new Color();
        // Reset flags
        laserDown = false;
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Find out if the laser is facing down
        laserDown = GameIntelligence.getInstance().isLaserDown(position);
        // Change its position
        position.set(position.x, Configuration.TERRAIN_ROOF_POSITION / 2);
        // Create the object
        super.create(position, movementId, 0, 0);
        // Set the laser rotation
        if(laserDown)
            setRotationInDegrees(180f);

        // Get the lightening texture
        TextureRegion lighteningTexture = Core.getInstance().getResourcesManager().getResource("LighteningAnimation",
                ResourceType.RESOURCE_TYPE_MODEL);

        // Create the lightening animation instance
        animation = new EntityAnimation(lighteningTexture, 2, 3, 0.05f, true);
        // Start the animation
        animation.start();
        // Calculate the laser size
        if(!laserDown)
        {
            laserSize.set(RAY_WIDTH, Configuration.TERRAIN_ROOF_POSITION - position.y + getSize().y / 2);
            laserPosition.set(getPosition().cpy().add(0, getSize().y / 2 + laserSize.y / 2));
        }
        else
        {
            laserSize.set(RAY_WIDTH, position.y - getSize().y / 2);
            laserPosition.set(new Vector2(getPosition().x, laserSize.y / 2));
        }
        // Setup the ray body
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(laserSize.x / 2, laserSize.y / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0;
        fixtureDef.shape = shape;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(laserPosition);

        // Create the ray body
        rayBody = GameWorld.getInstance().addToWorld(bodyDef);

        // Create the ray fixture
        rayBody.createFixture(fixtureDef);

        // Convert the size to pixels
        laserSize.set(GameMath.pixelsPerMeters(laserSize));
        // Set the lightening size
        lighteningSize.set(laserSize).scl(4f, laserDown ? 1.2f : 1f);
    }

    @Override
    public void destroy()
    {
        // Destroy the object
        super.destroy();
        // Destroy the ray body
        GameWorld.getInstance().removeFromWorld(rayBody);
    }

    @Override
    public void process()
    {
        // Process
        super.process();
        // Set the laser's position
        if(!laserDown)
            laserPosition.set(Camera.getInstance().worldToScreen(tmpVector.set(getPosition()).add(0, getSize().y / 2)).sub(laserSize.x / 2, 0));
        else
            laserPosition.set(Camera.getInstance().worldToScreen(tmpVector.set(getPosition().x, 0)).sub(laserSize.x / 2, 0));

        // Set the lightening position
        lighteningPosition.set(laserPosition).sub(laserSize.x, laserDown ? 0f : laserSize.y * 0.15f);
        // Update the animation's color
        animationColor.set(1f, 1f, 1f, getColor().a);
    }

    @Override
    public void render()
    {
        // Render the object
        super.render();
        // Set the laser's color
        Core.getInstance().getGraphicsManager().setColor(getColor());
        // Draw the laser
        Core.getInstance().getGraphicsManager().drawTexture(laserTexture, laserPosition, laserSize, 0f);
        // Restore the color
        Core.getInstance().getGraphicsManager().setColor(Color.WHITE);
        // Draw the animation
        animation.render(lighteningPosition, lighteningSize, 180f, animationColor);
    }


}
