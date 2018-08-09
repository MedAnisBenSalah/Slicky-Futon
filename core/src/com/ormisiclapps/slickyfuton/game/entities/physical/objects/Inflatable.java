package com.ormisiclapps.slickyfuton.game.entities.physical.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.ormisiclapps.slickyfuton.core.Core;
import com.ormisiclapps.slickyfuton.game.entities.physical.base.GameObject;

/**
 * Created by Anis on 11/18/2016.
 */

public class Inflatable extends GameObject
{
    private long lastResizeTime;
    private int resizingTime;
    private float resizingAnimationTime;
    private float targetSize;
    private float lastSize;
    private float targetLightDistance;
    private boolean resizing;
    private boolean inflated;
    private float initialSize;
    private float initialLightDistance;
    private boolean shouldRender;

    private static final int MAX_RESIZE_TIME = 2000;
    private static final int RESIZING_ANIMATION_TIME = 200;
    private static final int FLASHING_TIME = 800;

    public Inflatable()
    {
        // Create the inflatable object
        super("Inflatable", true);
        // Reset values
        lastResizeTime = 0;
        targetSize = 0;
        lastSize = 0;
        targetLightDistance = 0;
        initialSize = 0;
        initialLightDistance = 0;
        resizingTime = 0;
        resizingAnimationTime = 0;
        resizing = false;
        inflated = false;
        shouldRender = true;
    }

    @Override
    public void create(Vector2 position, int movementId, int startingPoint, float speed)
    {
        // Create the object
        super.create(position, movementId, startingPoint, speed);
        // Save the initial size
        initialSize = getRadius();
        // Save the initial light distance
        initialLightDistance = getLightDistance();
        // Calculate the resizing time
        calculateResizingTime();
        // Setup the last resize time
        lastResizeTime = TimeUtils.millis();
        // Reset flags
        inflated = MathUtils.randomBoolean();
        resizing = false;
    }

    @Override
    public void process()
    {
        // Process the object
        super.process();
        // Are we resizing
        if(resizing)
        {
            // Set the rendering flag
            shouldRender = true;
            // Get the size and light distance difference
            float sizeDifference = targetSize - lastSize;
            // Calculate the resizing factor
            float resizingFactor = RESIZING_ANIMATION_TIME / (Core.getInstance().getGraphicsManager().DELTA_TIME * 1000);
            // Resize the inflatable object
            resize(null, getRadius() + sizeDifference / resizingFactor);
            // Add the resizing time
            resizingAnimationTime += Core.getInstance().getGraphicsManager().DELTA_TIME * 1000;
            // Did we finish resizing ?
            if(resizingAnimationTime >= RESIZING_ANIMATION_TIME)
            {
                // Set the light distance
                setLightDistance(targetLightDistance);
                // Reset the resizing flag
                resizing = false;
                // Change the inflated flag
                inflated = !inflated;
                // Calculate the resizing time
                calculateResizingTime();
                // Setup the last resize time
                lastResizeTime = TimeUtils.millis();
            }
        }
        // Find out if we need to resize
        else if(TimeUtils.millis() - lastResizeTime >= resizingTime)
        {
            // Calculate the target size and light distance
            targetSize = inflated ? initialSize : initialSize * 1.925f;
            targetLightDistance = inflated ? initialLightDistance : initialLightDistance * 1.925f;
            // Save the current size and light distance
            lastSize = getRadius();
            // Reset resizing animation time
            resizingAnimationTime = 0;
            // Set the resizing flag
            resizing = true;
        }
        // Find out if we need to flash
        else if(TimeUtils.millis() - lastResizeTime >= resizingTime - FLASHING_TIME)
        {
            // Flash the object
            shouldRender = !shouldRender;
        }
    }

    private void calculateResizingTime()
    {
        // Calculate the resizing time based on randomness
        resizingTime = MathUtils.random(MAX_RESIZE_TIME / 3, MAX_RESIZE_TIME);
    }

    @Override
    public void render()
    {
        // Process flashing
        if(shouldRender)
            super.render();
    }
}
