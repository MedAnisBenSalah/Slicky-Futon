package com.ormisiclapps.slickyfuton.game.world;

import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;
import com.ormisiclapps.slickyfuton.game.entities.Camera;

/**
 * Created by OrMisicL on 9/11/2016.
 */
public class Lightening
{
    private RayHandler rayHandler;
    //private DirectionalLight environmentLight;
    //private ConeLight caveLight;
    private boolean active;

    private static Lightening instance;

    public Lightening(World world)
    {
        // Create the ray handler instance
        rayHandler = new RayHandler(world);
        // Setup ray handler
        rayHandler.setShadows(false);
        rayHandler.setBlur(false);
        // Create the environment light
       /* environmentLight = new DirectionalLight(rayHandler, Configuration.ENVIRONMENT_LIGHT_RAYS_NUMBER,
                Configuration.ENVIRONMENT_LIGHT_COLOR, Configuration.ENVIRONMENT_LIGHT_ANGLE);

        // Set the environment light parameters
        environmentLight.setActive(false);
        environmentLight.setStaticLight(true);
        // Remove collision to the rest of the entities
        environmentLight.setContactFilter((short)0x2, (short)0x2, (short)0x0);
        // Create the cave light
        caveLight = new ConeLight(rayHandler, Configuration.CAVE_LIGHT_RAYS_NUMBER, Configuration.CAVE_LIGHT_COLOR,
                Configuration.CAVE_LIGHT_DISTANCE, 0, 0, 0, Configuration.CAVE_LIGHT_FIELD_ROTATION);*/

        // Set flags
        active = true;
        // Set the instance
        instance = this;
    }

    public void render()
    {
        // Set the cave lights position to match the player's
        //caveLight.setPosition(Player.getInstance().getPosition());
        // Render lights
        if(active)
        {
            rayHandler.setCombinedMatrix(Camera.getInstance().getInterface());
            rayHandler.render();
        }
    }

    public void update()
    {
        // Update lights
        if(active)
            rayHandler.update();
    }

    public void destroy()
    {
        rayHandler.dispose();
    }

    public void toggleCaveLight(boolean toggle)
    {
        // Toggle the shadows (the dark environment effect)
       /* rayHandler.setShadows(toggle);
        // Toggle the environment light
        environmentLight.setActive(toggle);
        // Toggle the cave light (torch effect)
        caveLight.setActive(toggle);
        // Setup the lightening color if we're enabling cave mode
        if(toggle)
        {
            // Get the player's light color
            //Color playerLightColor = Player.getInstance().getModel().getNode().color;
            // Set the cave's light color to match the player's
            caveLight.setColor(new Color(1f, 1f, 1f, 0.6f));//(playerLightColor.r, playerLightColor.g, playerLightColor.b, Configuration.CAVE_LIGHT_COLOR_ALPHA));
        }*/
    }

    public RayHandler getHandler() { return rayHandler; }

    public void toggleLights(boolean toggle)
    {
        // Set the active flag
        active = toggle;
    }

    public static Lightening getInstance()
    {
        return instance;
    }
}
