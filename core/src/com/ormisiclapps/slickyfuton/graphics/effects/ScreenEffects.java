package com.ormisiclapps.slickyfuton.graphics.effects;

import com.badlogic.gdx.graphics.Color;
import com.ormisiclapps.slickyfuton.core.Core;

/**
 * Created by OrMisicL on 1/5/2016.
 * Will handle general screen effects (flushing, fading)
 */
public class ScreenEffects
{
    private boolean transitioning;
    private long transitionTime;
    private long transitionDuration;
    private Color transitionColor;
    private boolean flushing;
    private boolean faded;
    private Color fadeColor;

    public ScreenEffects()
    {
        // Reset flags
        transitioning = false;
        faded = false;
        flushing = false;
        // Reset values
        transitionTime = 0;
        transitionDuration = 0;
        // Create the color instances
        transitionColor = new Color();
        fadeColor = new Color();
    }

    public void transit(long duration)
    {
        // Toggle transitioning
        transitioning = true;
        // Reset the time
        transitionTime = 0;
        // Set the duration
        transitionDuration = duration;
        // Set the starting color
        transitionColor.set(0, 0, 0, 0);
    }

    public void flush()
    {
        // Set the flushing flag
        flushing = true;
    }

    public void resetFlush()
    {
        // Reset the flushing flag
        flushing = false;
    }

    public void fadeIn(Color color, float alpha)
    {
        // Ensure the we're not doing any other effects
        if(flushing || transitioning)
            return;

        // Set the fade parameters
        faded = true;
        fadeColor.set(color.r, color.g, color.b, alpha);
    }

    public void fadeOut()
    {
        // Ensure the we're already faded
        if(!faded)
            return;

        // Reset the fade parameters
        faded = false;
    }

    public void update()
    {
        // Update the screen transition
        if(transitioning)
        {
            // Ignore a big delta time value
            if(Core.getInstance().getGraphicsManager().DELTA_TIME >= 0.2f)
                return;

            // Update the transition time
            transitionTime += Core.getInstance().getGraphicsManager().DELTA_TIME * 1000;
            // Set the color depending on the translation time
            if(!isTransitionFadeOut())
                transitionColor.set(0, 0, 0, (float)transitionTime / (transitionDuration / 2f));
            else
                transitionColor.set(0, 0, 0, 2f - (float)transitionTime / (transitionDuration / 2f));

            // Finish translating if necessary
            if(transitionTime >= transitionDuration)
                transitioning = false;
        }
    }

    public void haltTransition()
    {
        // Reset transitioning flag
        transitioning = false;
    }

    public boolean isTransitioning() { return transitioning; }

    public boolean isTransitionFadeOut()
    {
        return transitioning && transitionTime >= transitionDuration / 2;
    }

    public Color getTransitionColor() { return transitionColor; }

    public boolean isFlushing() {
        return flushing;
    }

    public boolean isFaded() { return faded; }

    public Color getFadeColor() { return fadeColor; }
}
